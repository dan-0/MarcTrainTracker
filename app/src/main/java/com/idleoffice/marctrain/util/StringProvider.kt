package com.idleoffice.marctrain.util

import android.content.Context
import androidx.annotation.StringRes

class StringProvider(private val context: Context) {

    fun getString(@StringRes res: Int) = context.getString(res)
}