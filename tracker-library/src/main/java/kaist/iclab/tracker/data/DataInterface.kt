package kaist.iclab.tracker.data

interface DataInterface {
    // Server IP
    fun init()


    fun insert()
    fun update()
    fun delete()

    fun sync()
}