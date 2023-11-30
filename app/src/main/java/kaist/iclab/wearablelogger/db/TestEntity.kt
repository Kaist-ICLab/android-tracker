package kaist.iclab.wearablelogger.db

import androidx.room.Entity

@Entity(
    tableName = "testEvent",
    primaryKeys = ["timestamp"]
)
data class TestEntity(
    var timestamp: Long,
)
