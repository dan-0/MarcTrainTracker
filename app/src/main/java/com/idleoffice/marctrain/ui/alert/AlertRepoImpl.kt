package com.idleoffice.marctrain.ui.alert

import com.idleoffice.marctrain.idling.IdlingResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import timber.log.Timber

class AlertRepoImpl (
    private val idlingResource: IdlingResource
) : AlertRepo {

    private val _data = MutableStateFlow<AlertRepoState>(AlertRepoState.Init)
    override val data: StateFlow<AlertRepoState> = _data

    override suspend fun fetchAlertData() {
        idlingResource.startIdlingAction()
        val mainDoc = Jsoup.connect("$BASE_MTA_URL$SERVICE_ALERT_PATH").get()

        val alertsRows = mainDoc.getElementById(ACTIVE_ALERT_ID)?.children()

        if (alertsRows.isNullOrEmpty()) {
            Timber.e("Empty alert rows")
            return
        }

        val marcAlerts: List<Element> = alertsRows.mapNotNull {
            if (it.childNodeSize() < 2) {
                Timber.e("Unexpected node size of serviceAlertTd: ${it.data()}")
                return@mapNotNull null
            }

            val descriptionChild = it.child(1)
            if (!descriptionChild.text().contains("MARC:", true)) {
                return@mapNotNull null
            }

            it
        }

        val basicAlerts = marcAlerts.mapNotNull {
            val dataNodes = it.childNodes().filterNot { node -> node is TextNode }
            if (dataNodes.size != 3) {
                Timber.e("Expected child node missing: $dataNodes")
                return@mapNotNull null
            }

            val titleElement = dataNodes[0] as? Element

            if (titleElement?.childNodeSize() != 1) {
                Timber.e("No link node found: $titleElement")
                return@mapNotNull null
            }

            val link = titleElement.childNode(0).attr("abs:href")
            val description = titleElement.text()

            val lineNode = dataNodes[1] as? Element
            val line = lineNode?.text()?.substringAfter(MARC_LABEL_DELIMITER)

            val dateNode = dataNodes[2] as? Element
            val date = dateNode?.text()

            if (line.isNullOrBlank() || date.isNullOrBlank() || link.isNullOrBlank()) {
                Timber.e("Missing link, line or date nodes: $dataNodes")
                return@mapNotNull null
            }

            BasicAlert(
                description = description,
                line = line,
                dateTime = date,
                url = link
            )
        }

        _data.value = AlertRepoState.Content(basicAlerts)
        idlingResource.stopIdlingAction()
    }

    companion object {
        const val ACTIVE_ALERT_ID = "active-alerts"
        const val BASE_MTA_URL = "https://mta.maryland.gov"
        const val SERVICE_ALERT_PATH = "/service-alerts"

        private const val MARC_LABEL_DELIMITER = ": "
    }
}