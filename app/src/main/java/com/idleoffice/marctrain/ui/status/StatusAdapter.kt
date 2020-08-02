/*
 * Copyright (c) 2019 IdleOffice Inc.
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
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.idleoffice.marctrain.ui.status

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.databinding.CardStatusTrainBinding

class StatusAdapter : ListAdapter<TrainStatus, TrainStatusViewHolder>(TrainStatusEquality) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : TrainStatusViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardStatusTrainBinding.inflate(inflater, parent, false)

        return TrainStatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainStatusViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item as TrainStatus)
    }

}

