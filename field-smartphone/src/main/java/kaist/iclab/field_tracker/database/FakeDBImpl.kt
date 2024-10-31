//package kaist.iclab.tracker.database
//
//import android.util.Log
//
//class FakeDBImpl : DatabaseInterface {
//    companion object {
//        private val TAG = "FakeDBImpl"
//    }
//
//    override fun insert(collectionName: String, data: Map<String, Any>): String {
//        Log.d(TAG, "insert: $collectionName, ${Util.map2Json(data)}")
//        return "SUCCESS"
//    }
//
//    override fun queryAllDocs(collectionName: String): List<String> {
//        return listOf()
//    }
//}