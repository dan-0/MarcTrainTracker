package com.idleoffice.marctrain.ui.schedule

import com.idleoffice.marctrain.analytics.FirebaseService
import com.idleoffice.marctrain.data.AppAction
import com.idleoffice.marctrain.data.AppEvent

class FakeAnalyticsService : FirebaseService {

    override fun newEvent(event: AppEvent) {
        /* stub */
    }

    override fun newAction(action: AppAction) {
        /* stub */
    }

}