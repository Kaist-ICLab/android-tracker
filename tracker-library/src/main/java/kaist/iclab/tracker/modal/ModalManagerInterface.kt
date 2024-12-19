package kaist.iclab.tracker.modal

interface ModalManagerInterface {
    fun showModal(
        title: String,
        message: String,
        onResult: (accepted: Boolean) -> Unit,
    )
}