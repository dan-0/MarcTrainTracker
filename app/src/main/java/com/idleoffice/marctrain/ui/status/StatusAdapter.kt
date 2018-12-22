/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * StatusAdapter.kt is part of MarcTrainTracker.
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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain.ui.status

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.ui.base.BaseViewHolder
import kotlinx.android.synthetic.main.recycler_status_train.view.*
import timber.log.Timber

class StatusAdapter(val trainStatuses: MutableList<TrainStatus>) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : BaseViewHolder {
        val statusLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_status_train, parent, false) as ConstraintLayout

        return ViewHolder(statusLayout)
    }

    override fun getItemCount() : Int {
        return trainStatuses.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(statusTrain: ConstraintLayout) : BaseViewHolder(statusTrain) {

        private val trainNumber : TextView = statusTrain.trainNumber
        private val trainNextStation : TextView = statusTrain.textDataStationName
        private val trainDepart : TextView = statusTrain.textDataDepart
        private val trainStatus : TextView = statusTrain.textDataStatus
        private val trainDelay : TextView? = statusTrain.textDataDelay
        private val trainDelayLabel : TextView? = statusTrain.textLabelDelay

        override fun onBind(position: Int) {
            val st = trainStatuses[position]
            Timber.d("binding $st")
            trainNumber.text = st.number
            trainNextStation.text = st.nextStation
            trainDepart.text = st.departure
            trainStatus.text = st.status
            if (st.delay.isEmpty()) {
                Timber.d("Removing delay")
                val tdp = trainDelay?.parent as? ViewManager
                tdp?.removeView(trainDelay)
                val tdlp = trainDelayLabel?.parent as? ViewManager
                tdlp?.removeView(trainDelayLabel)
            }
            trainDelay?.text = st.delay
        }
    }
}