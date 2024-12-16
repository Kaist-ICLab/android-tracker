package kaist.iclab.tracker.database

import kotlinx.coroutines.flow.Flow

interface DatabaseInterface {

//    fun insert(name: String, data: AbstractCollector.DataEntity)
//
//    /* set server address*/
//    suspend fun setServer(address: Uri)
//
//    /**/
//    suspend fun sync()
//
//    /* Delete all data */
//    fun flush()
//
//    /*Export all data*/
//    /*TODO: maybe UI required*/
//    fun export()
//
    /*Update config including their enabled/disabled*/
//    fun updateConfig(name: String, config: AbstractCollector.Config)


    fun updateConfig(name: String, value: Boolean)
    fun getConfigFlow(): Flow<Map<String, Boolean>>
}