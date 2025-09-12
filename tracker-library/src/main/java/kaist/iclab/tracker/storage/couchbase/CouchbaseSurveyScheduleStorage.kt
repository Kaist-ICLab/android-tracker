package kaist.iclab.tracker.storage.couchbase

import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Expression
import com.couchbase.lite.Function
import com.couchbase.lite.Meta
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Ordering
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import kaist.iclab.tracker.TrackerUtil.formatLocalDateTime
import kaist.iclab.tracker.sensor.survey.SurveySchedule
import kaist.iclab.tracker.storage.core.SurveyScheduleStorage
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import java.util.concurrent.TimeUnit

class CouchbaseSurveyScheduleStorage(
    couchbase: CouchbaseDB,
    collectionName: String,
): SurveyScheduleStorage {
    companion object {
        private val TAG = CouchbaseSurveyScheduleStorage::class.simpleName
    }

    private val collection = couchbase.getCollection(collectionName)

    override fun isTodayScheduleExist(): Boolean {
        val zoneId = ZoneId.systemDefault()
        val todayStart = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val todayEnd = todayStart + TimeUnit.DAYS.toMillis(1)

        val query = QueryBuilder.select(SelectResult.expression(Function.count(Expression.string("*"))).`as`("totalCount"))
            .from(DataSource.collection(collection))
            .where(Expression.property("triggerTime").between(
                Expression.longValue(todayStart),
                Expression.longValue(todayEnd)
            ))

        return try {
            query.execute().first().getLong("totalCount") > 0
        } catch(_: NoSuchElementException) {
            false
        }
    }

    override fun getNextSchedule(): SurveySchedule? {
        Log.d(TAG, "Today schedule exist? ${isTodayScheduleExist()}")
        val now = System.currentTimeMillis()
        val query = QueryBuilder.select(SelectResult.expression(Meta.id).`as`("uuid"), SelectResult.all())
            .from(DataSource.collection(collection))
            .where(
                Expression.property("isExecuted").equalTo(Expression.booleanValue(false))
                    .and(Expression.property("triggerTime").greaterThanOrEqualTo(Expression.longValue(now)))
            )
            .orderBy(Ordering.property("triggerTime").ascending())
            .limit(Expression.intValue(1))

        try {
            val result = query.execute().use {
                val result = it.first()
                val docUuid = result.getString("uuid")!!

                val resultDict = result.getDictionary(collection.name)
                if(resultDict == null) null else SurveySchedule(
                    uuid = docUuid,
                    surveyId = resultDict.getString("surveyId") ?: "",
                    triggerTime = resultDict.getLong("triggerTime"),
                    isExecuted = resultDict.getBoolean("isExecuted")
                )
            }

            if(result != null) Log.d(TAG, "Next Schedule: surveyId=${result.surveyId}, uuid=${result.uuid!!}, triggerTime=${result.triggerTime.formatLocalDateTime()}")
            else Log.d(TAG, "No next schedule today")

            return result

        } catch(e: NoSuchElementException) {
            e.printStackTrace()
            return null
        } catch(e: NullPointerException) {
            e.printStackTrace()
            return null
        }
    }

    override fun addSchedule(schedule: SurveySchedule) {
        Log.d(TAG, "Schedule added: ${schedule.triggerTime.formatLocalDateTime()}")

        val mutableDoc = MutableDocument(UUID.randomUUID().toString())
        mutableDoc.apply {
            setString("surveyId", schedule.surveyId)
            setLong("triggerTime", schedule.triggerTime)
            setBoolean("isExecuted", schedule.isExecuted)
        }

        collection.save(mutableDoc)
    }

    override fun markExecuted(uuid: String) {
        val doc = collection.getDocument(uuid)?.toMutable()!!

        doc.setBoolean("isExecuted", true)
        doc.setLong("actualTriggerTime", System.currentTimeMillis())
        collection.save(doc)
    }

    fun resetSchedule() {
        val query = QueryBuilder.select(SelectResult.expression(Meta.id).`as`("uuid"))
            .from(DataSource.collection(collection))

        query.execute().use {
            for(result in it) {
                val docUuid = result.getString("uuid")!!
                val docToDelete = collection.getDocument(docUuid)

                if(docToDelete != null) collection.delete(docToDelete)
            }
        }
    }
}