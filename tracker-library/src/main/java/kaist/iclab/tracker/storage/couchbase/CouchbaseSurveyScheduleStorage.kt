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
            .where(
                Expression.property("triggerTime").between(
                Expression.longValue(todayStart),
                Expression.longValue(todayEnd)
            ).and(Expression.property("actualTriggerTime").isNotValued())
            )

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
                Expression.property("triggerTime").greaterThanOrEqualTo(Expression.longValue(now))
            )
            .orderBy(Ordering.property("triggerTime").ascending())
            .limit(Expression.intValue(1))

        try {
            val result = query.execute().use {
                val result = it.first()
                val docUuid = result.getString("uuid")!!

                val resultDict = result.getDictionary(collection.name)
                if(resultDict == null) null else SurveySchedule(
                    scheduleId = docUuid,
                    surveyId = resultDict.getString("surveyId") ?: "",
                    triggerTime = resultDict.getLong("triggerTime"),
                )
            }

            if(result != null) Log.d(TAG, "Next Schedule: surveyId=${result.surveyId}, uuid=${result.scheduleId!!}, triggerTime=${result.triggerTime?.formatLocalDateTime()}")
            else Log.d(TAG, "No next schedule today")

            return result

        } catch(e: Exception) {
            if(e is NoSuchElementException) Log.d(TAG, "No next schedule today")
            else e.printStackTrace()
            return null
        }
    }

    override fun getScheduleByScheduleId(scheduleId: String): SurveySchedule? {
        val query = QueryBuilder.select(SelectResult.expression(Meta.id).`as`("uuid"), SelectResult.all())
            .from(DataSource.collection(collection))
            .where(Expression.property("uuid").equalTo(Expression.string(scheduleId)))

        try {
            val result = query.execute().use {
                val result = it.first()
                val docUuid = result.getString("uuid")!!

                val resultDict = result.getDictionary(collection.name)
                if (resultDict == null) null else SurveySchedule(
                    scheduleId = docUuid,
                    surveyId = resultDict.getString("surveyId") ?: "",
                    triggerTime = resultDict.getLong("triggerTime"),
                    actualTriggerTime = resultDict.getLong("actualTriggerTime"),
                    surveyStartTime = resultDict.getLong("surveyStartTime"),
                    responseSubmissionTime = resultDict.getLong("responseSubmissionTime"),
                )
            }

            if(result != null) Log.d(TAG, "Schedule Found: surveyId=${result.surveyId}, uuid=${result.scheduleId!!}")
            else Log.d(TAG, "No corresponding schedule to scheduleId=$scheduleId")

            return result

        } catch(e: NoSuchElementException) {
            e.printStackTrace()
            return null
        } catch(e: NullPointerException) {
            e.printStackTrace()
            return null
        }
    }

    override fun addSchedule(schedule: SurveySchedule): String {
        Log.d(TAG, "Schedule added: ${schedule.triggerTime?.formatLocalDateTime()}")
        val uuid = UUID.randomUUID().toString()

        val mutableDoc = MutableDocument(uuid)
        mutableDoc.apply {
            setString("surveyId", schedule.surveyId)
            if(schedule.triggerTime != null) setLong("triggerTime", schedule.triggerTime)
        }

        collection.save(mutableDoc)
        return uuid
    }

    override fun setActualTriggerTime(scheduleId: String, timestamp: Long) {
        val doc = collection.getDocument(scheduleId)?.toMutable()!!
        doc.setLong("actualTriggerTime", timestamp)
        collection.save(doc)
    }

    override fun setSurveyStartTime(scheduleId: String, timestamp: Long) {
        val doc = collection.getDocument(scheduleId)?.toMutable()!!
        doc.setLong("surveyStartTime", timestamp)
        collection.save(doc)
    }

    override fun setResponseSubmissionTime(scheduleId: String, timestamp: Long) {
        val doc = collection.getDocument(scheduleId)?.toMutable()!!
        doc.setLong("responseSubmissionTime", timestamp)
        collection.save(doc)
    }

    override fun resetSchedule() {
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