import com.intellij.ide.BrowserUtil
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor

/**
 * Extension of [AnAction].
 *
 * It takes the incoming parameter from the event and launches the browser with appropriate
 * search engine.
 */
class SearchKeyword: AnAction() {

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val editor = getEditor(actionEvent)
        if (editor?.selectionModel?.hasSelection() == true) {
            var success = false
            when(actionEvent.presentation.text.lowercase()) {
                Constants.GOOGLE -> {
                    success = launchBrowser(Constants.GOOGLE_LINK, getSearchKeyword(actionEvent))
                }
                Constants.STACK_OVERFLOW -> {
                    success = launchBrowser(Constants.STACK_OVERFLOW_LINK, getSearchKeyword(actionEvent))
                }
                Constants.MEDIUM -> {
                    success = launchBrowser(Constants.MEDIUM_LINK, getSearchKeyword(actionEvent))
                }
            }

            if(!success) showErrorNotification(actionEvent)
        } else {
            showErrorNotification(actionEvent)
        }
    }

    /**
     * Since our plugin is editor dependent, enable the plugin only if an editor is open and some text is
     * initially selected.
     */
    override fun update(actionEvent: AnActionEvent) {
        actionEvent.presentation.isEnabledAndVisible = actionEvent.project != null && !getEditor(actionEvent)?.caretModel?.allCarets.isNullOrEmpty()
    }

    /**
     * Get [Editor] instance for current [AnAction].
     *
     * @return [Editor] instance.
     */
    private fun getEditor(actionEvent: AnActionEvent): Editor? {
        return actionEvent.getData(CommonDataKeys.EDITOR)
    }

    /**
     * In case no text is selected and user tries to use the plugin, show this error notification.
     *
     * Note:- Need to update this implementation to be compatible with future versions.
     */
    private fun showErrorNotification(actionEvent: AnActionEvent) {
        val content = "No text is selected. Please select some text to look up."
        NotificationGroupManager.getInstance().getNotificationGroup(Constants.NOTIFICATION_GROUP)
            .createNotification(content, NotificationType.INFORMATION)
            .notify(actionEvent.project)
    }

    /**
     * Launch the browser with appropriate search query.
     *
     * @param baseUrl for the search engine selected.
     * @param searchKeyword text selected for search.
     */
    private fun launchBrowser(baseUrl: String, searchKeyword: String?): Boolean {
        if (searchKeyword.isNullOrEmpty()) return false

        BrowserUtil.browse("$baseUrl/search?q=android $searchKeyword")
        return true
    }

    /**
     * @return search keyword string from given [actionEvent].
     */
    private fun getSearchKeyword(actionEvent: AnActionEvent): String? {
        return getEditor(actionEvent)?.caretModel?.currentCaret?.selectedText
    }
}