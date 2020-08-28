/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * AlertAdapter.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.ui.alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.idleoffice.marctrain.databinding.RecyclerAlertTrainBinding
import java.text.SimpleDateFormat
import java.util.*

class AlertAdapter : ListAdapter<BasicAlert, BasicAlertViewHolder>(AlertEquality) {

    val fromDateFormatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : BasicAlertViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = RecyclerAlertTrainBinding.inflate(inflater, parent, false)

        return BasicAlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BasicAlertViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }
}

class BasicAlertViewHolder(
    private val binding: RecyclerAlertTrainBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(alert: BasicAlert) {
        binding.line.text = alert.line
        binding.description.text = alert.description
        binding.date.text = alert.dateTime
    }

}