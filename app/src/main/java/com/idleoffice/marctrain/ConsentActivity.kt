/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * ConsentActivity.kt is part of MarcTrainTracker.
 *
 * MarcTrainTracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MarcTrainTracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.idleoffice.marctrain

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.idleoffice.idleconsent.IdleConsent
import com.idleoffice.idleconsent.IdleConsentCallback
import com.idleoffice.idleconsent.IdleConsentConfig
import com.idleoffice.idleconsent.IdleInfoSource
import com.idleoffice.marctrain.ui.main.MainActivity

class ConsentActivity : AppCompatActivity() {

    private val idleConfig = IdleConsentConfig(
            consentTitle = "Disclosures",
            introStatement = "Please take a moment to read through our privacy disclosure and terms of service.",
            dataCollectedSummary = "To ensure the best experience we collect the following anonymized user data to inform uf of crashes and how our users interact with the app:",
            dataCollected = listOf("Device information", "Usage statistics", "Advertising ID"),
            privacyInfoSource = IdleInfoSource.Web(
                    "Please see our full privacy policy.",
                    Uri.parse("https://marctrain.app/privacy/")
            ),
            requirePrivacy = true,
            acceptPrivacyPrompt = "Please take a moment and read our privacy policy",
            privacyPromptChecked = true,
            termsSummary = "Please take the time to look at our terms and conditions:",
            termsInfoSource = IdleInfoSource.Web("See full terms and conditions", Uri.parse("https://marctrain.app/terms/")),
            version = 1
    )

    private val consentCallback = object : IdleConsentCallback() {
        override fun onAcknowledged(hasUserAgreedToTerms: Boolean, hasUserAgreedToPrivacy: Boolean) {
            if (hasUserAgreedToTerms) {
                startMainActivity()
            }
        }
    }

    private fun startMainActivity() {
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }
        Intent(this@ConsentActivity, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consent)
        val consent = IdleConsent(this)
        if (!consent.hasUserAgreedToTerms() || consent.isNewConsentVersion(idleConfig.version)) {
            consent.showConsentDialog(supportFragmentManager, consentCallback, idleConfig)
        } else {
            startMainActivity()
        }
    }

}