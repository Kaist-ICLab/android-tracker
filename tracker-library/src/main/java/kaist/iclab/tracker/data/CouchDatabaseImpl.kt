package kaist.iclab.tracker.data

import android.content.Context
import android.util.Log
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.Meta
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Query
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.collector.core.DataEntity
import kaist.iclab.tracker.data.core.DatabaseInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.StringWriter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class CouchDatabaseImpl(
    private val context: Context,
) : DatabaseInterface {
    private val TAG = javaClass.simpleName
    private val DB = "TRACKER"
    private val SERVER_CONFIG = "SERVER_CONFIG"
    private val COLLECTOR_STATE = "COLLECTOR_STATE"
    private val COLLECTOR_CONFIG = "COLLECTOR_CONFIG"

    init {
        // Initialize Couchbase Lite
        CouchbaseLite.init(context)
        refreshCollectorConfigFlow()
        refreshCollectorStateFlow()
    }

    private val database: Database by lazy {
        Database(DB)
    }


    private val _serverAddressFlow = MutableStateFlow(getServerAddress())
    override val serverAddressFlow: StateFlow<String?> = _serverAddressFlow.asStateFlow()
    override fun registerServer(serverAddress: String) {
        val collection = getCollection(SERVER_CONFIG)
        // 서버 ID를 저장할 문서 생성
        val serverDocumentId = "server_address"
        val mutableDocument = collection.getDocument(serverDocumentId)?.toMutable()
            ?: MutableDocument(serverDocumentId)
        mutableDocument.setString("address", serverAddress)
        collection.save(mutableDocument)
        _serverAddressFlow.value = serverAddress
    }

    private fun getServerAddress(): String? {
        val collection = getCollection(SERVER_CONFIG)
        val document = collection.getDocument("server_address")
        return document?.getString("address")
    }

    override fun export(outputDirPath: String) {
        val outputDir = File(outputDirPath)

        // 타임스탬프를 이용한 ZIP 파일 이름 생성
        val timestamp = System.currentTimeMillis()
        val zipFileName = "tracker_csv_$timestamp.zip"
        val zipFilePath = File(outputDir, zipFileName)

        ZipOutputStream(FileOutputStream(zipFilePath)).use { zipOut ->
            // 컬렉션별 CSV 데이터를 가져오기
            val csvStrings = db2CSVs()
            for ((collectionName, csvContent) in csvStrings) {
                // 각 컬렉션을 CSV 파일로 ZIP에 추가
                val zipEntry = ZipEntry("$collectionName.csv")
                zipOut.putNextEntry(zipEntry)
                // CSV 내용을 ZIP에 쓰기
                val byteArray = csvContent.toByteArray(Charsets.UTF_8)
                zipOut.write(byteArray, 0, byteArray.size)
                zipOut.closeEntry()
            }
        }
    }

    override fun log(tag:String, message: String) {
        Log.d(tag, message)
    }

    override fun updateCollectorConfig(name: String, config: CollectorConfig) {
        val collection = getCollection(COLLECTOR_CONFIG)

        val id = name + "Config"
        val mutableDocument = collection.getDocument(id)?.toMutable()
            ?: MutableDocument(id)

        val gson = Gson()
        mutableDocument.setString("config", gson.toJson(config).toString())
        mutableDocument.setString("type", config::class.simpleName)
        collection.save(mutableDocument)
    }

    private fun refreshCollectorConfigFlow() {
        val allConfigs = mutableMapOf<String, String>()
        val collection = getCollection(COLLECTOR_CONFIG)

        val query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.collection(collection))

        query.execute().use { result ->
            for (row in result) {
                val document = row.getDictionary(collection.name)
                val name = row.getString("id")?.removeSuffix("Config") ?: continue
                row.getString("config")?.let { config ->
                    allConfigs[name] = config
                }
            }
        }
        _collectorConfigFlow.value = allConfigs
    }

    private val _collectorConfigFlow: MutableStateFlow<Map<String, String>> = MutableStateFlow(emptyMap())
    override val collectorConfigFlow: StateFlow<Map<String, String>> = _collectorConfigFlow.asStateFlow()


    override fun updateCollectorState(name: String, state: CollectorState) {
        val collection = getCollection(COLLECTOR_STATE)
        val id = name + "State"
        val mutableDocument = collection.getDocument(id)?.toMutable()
            ?: MutableDocument(id)
        mutableDocument.setString("flag", state.flag.name)
        collection.save(mutableDocument)
    }

    private fun refreshCollectorStateFlow() {
        val allConfigs = mutableMapOf<String, String>()
        val collection = getCollection(COLLECTOR_STATE)

        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))

        query.execute().use { result ->
            for (row in result) {
                val name = row.getString("id")?.removeSuffix("Config") ?: continue
                row.getString("flag")?.let { config ->
                    allConfigs[name] = config
                }
            }
        }
        _collectorConfigFlow.value = allConfigs
    }

    private val _collectorStateFlow: MutableStateFlow<Map<String, String>> = MutableStateFlow(emptyMap())
    override val collectorStateFlow: StateFlow<Map<String, String>> = _collectorStateFlow.asStateFlow()

    private fun pascalToUpperSnake(name: String): String {
        return name.replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .uppercase()
    }

    override fun insert(name: String, data: DataEntity) {
        val map = convertDataEntityToMap(data)
        val document = MutableDocument(map)
        val collection = getCollection(name)
        collection.save(document)
    }

    override fun update(name: String, id: String, data: DataEntity) {
        val collection = getCollection(name)
        val doc = collection.getDocument(id)
        if (doc != null) {
            val map = convertDataEntityToMap(data)
            val mutableDoc = doc.toMutable()
            for (entry in map.entries) {
                mutableDoc.setValue(entry.key, entry.value)
            }
            collection.save(mutableDoc)
        }
    }

    override fun delete(name: String, id: String) {
        val collection = getCollection(name)
        val doc = collection.getDocument(id)
        if (doc != null) {
            collection.delete(doc)
        }
    }

    override fun updateSyncStatus(ids: List<String>) {
        val timestamp = System.currentTimeMillis()
        for (id in ids) {
            val (name, docId) = id.split(" ", limit = 2)
            val collection = getCollection(name)
            val doc = collection.getDocument(docId)
            if (doc != null) {
                val mutableDoc = doc.toMutable()
                mutableDoc.setLong("synced", timestamp)
                collection.save(mutableDoc)
            }
        }
    }

    private fun db2CSVs(): Map<String, String> {
        val csvMap = mutableMapOf<String, String>()
        for (collection in database.collections) {
            val writer = StringWriter()

            try {
                val query =
                    QueryBuilder.select(SelectResult.expression(Meta.id), SelectResult.all())
                        .from(DataSource.collection(collection))

                val headers = mutableSetOf<String>() // 문서의 모든 필드를 헤더로 정리하기 위해 사용
                val rows = mutableListOf<Map<String, Any>>() // 문서 데이터를 저장
                query.execute().use { result ->
                    for (row in result) {
                        val document = row.getDictionary(collection.name)
                        document?.let {
                            val rowData = mutableMapOf<String, Any>()
                            rowData["ID"] = row.getString("id") ?: ""
                            document.keys.forEach { key ->
                                rowData[key] = document.getValue(key) ?: ""
                                headers.add(key) // 헤더에 추가
                            }
                            rows.add(rowData)
                        }
                    }
                }

                // CSV 헤더 작성
                val sortedHeaders = listOf("ID") + headers.sorted()
                writer.append(sortedHeaders.joinToString(","))
                writer.append("\n")

                // CSV 데이터 작성
                rows.forEach { row ->
                    val line = sortedHeaders.joinToString(",") { key -> row[key]?.toString() ?: "" }
                    writer.append(line)
                    writer.append("\n")
                }
                csvMap[collection.name] = writer.toString()
            } catch (e: Exception) {
                println("Error exporting collection '${collection.name}': ${e.message}")
            } finally {
                writer.close()
            }
        }
        return csvMap
    }

    override fun db2Json(unsyncedOnly: Boolean): Pair<String, List<String>> {
        val sendString = JSONObject()
        val containedId = mutableListOf<String>()
        for (collection in database.collections) {
            val jsonArray = JSONArray()
            val query: Query = if (unsyncedOnly) {
                QueryBuilder.select(SelectResult.all())
                    .from(DataSource.collection(collection))
                    .where(Expression.property("synced").equalTo(Expression.longValue(-1)))
            } else {
                QueryBuilder.select(SelectResult.all())
                    .from(DataSource.collection(collection))
            }
            query.execute().use { result ->
                for (row in result) {
                    val document = row.getDictionary(0)
                    val id = row.getString("id") ?: continue
                    containedId.add(collection.name + " " + id)
                    val jsonObject = JSONObject()
                    document?.keys?.forEach { key -> jsonObject.put(key, document.getValue(key)) }
                    jsonArray.put(jsonObject)
                }
            }
            sendString.put(collection.name, jsonArray)
        }
        return Pair(sendString.toString(), containedId)
    }


    private fun getCollection(collectionName: String): com.couchbase.lite.Collection {
        val collection = database.getCollection(collectionName)
            ?: database.createCollection(collectionName)
        return collection
    }


    private fun convertDataEntityToMap(data: DataEntity): Map<String, Any> {
        val gson = Gson()
        val jsonString = gson.toJson(data)
        val map: Map<String, Any> =
            gson.fromJson(jsonString, object : TypeToken<Map<String, Any>>() {}.type)
        return map
    }
}