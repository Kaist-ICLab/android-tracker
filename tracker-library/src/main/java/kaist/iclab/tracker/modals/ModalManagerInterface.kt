package kaist.iclab.tracker.modals

interface ModalManagerInterface {
    fun showModal(
        title: String,
        message: String,
        onResult: (accepted: Boolean) -> Unit,
    )
}