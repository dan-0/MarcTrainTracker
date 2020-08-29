package com.idleoffice.marctrain.ui.alertdetails

import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.idling.IdlingResource
import com.idleoffice.marctrain.util.StringProvider
import com.idleoffice.marctrain.util.toplevel.logEIllegalArgument
import com.idleoffice.marctrain.util.toplevel.logWIllegalArgument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber

class AlertDetailsRepoImpl(
    private val stringProvider: StringProvider,
    private val idlingResource: IdlingResource
) : AlertDetailsRepo {

    private val _data = MutableStateFlow<AlertDetailsRepoState>(AlertDetailsRepoState.Init)
    override val data: StateFlow<AlertDetailsRepoState> = _data

    override suspend fun loadDetails(url: String) {
        idlingResource.startIdlingAction()

        val mainDoc = runCatching {
            Jsoup.connect(url).get()
        }.getOrElse {
            Timber.w(it)
            _data.value = AlertDetailsRepoState.Error
            idlingResource.stopIdlingAction()
            return
        }

        val title = mainDoc.getElementsByClass("title").firstOrNull()
            ?.text()
            ?: run {
                logEIllegalArgument("Title not found")
                stringProvider.getString(R.string.alert)
            }

        val startDate = mainDoc.getContentText(CLASS_START_DATE)
        val endDate = mainDoc.getContentText(CLASS_END_DATE)
        val description = mainDoc.getContentText(CLASS_DESCRIPTION)
        val affectedRoutes = mainDoc.getContentText(CLASS_AFFECTED_ROUTES)
        val cause = mainDoc.getContentText(CLASS_CAUSE)
        val effect = mainDoc.getContentText(CLASS_EFFECT)

        val alertDetails = AlertDetails(
            title = title,
            description = description,
            startDate = startDate,
            endDate = endDate,
            affectedRoutes = affectedRoutes,
            cause = cause,
            effect = effect
        )

        _data.value = AlertDetailsRepoState.Content(alertDetails)
        idlingResource.stopIdlingAction()
    }

    private fun Document.getContentText(parentClassName: String): String {
        val notProvidedText = stringProvider.getString(R.string.not_provided)

        return getElementsByClass(parentClassName).let {
            it.firstOrNull()?.getElementsByClass("content")
        }?.let {
            it.firstOrNull()?.text()
        } ?: run {
            logWIllegalArgument("Error finding parentClassName: $parentClassName")
            notProvidedText
        }
    }

    companion object {
        private const val CLASS_START_DATE = "alert_start_date"
        private const val CLASS_END_DATE = "alert_end_date"
        private const val CLASS_DESCRIPTION = "description"
        private const val CLASS_AFFECTED_ROUTES = "affected_routes"
        private const val CLASS_CAUSE = "alert_cause"
        private const val CLASS_EFFECT = "alert_effect"
    }

}