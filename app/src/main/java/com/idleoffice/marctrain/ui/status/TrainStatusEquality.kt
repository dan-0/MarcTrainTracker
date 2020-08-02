package com.idleoffice.marctrain.ui.status

import androidx.recyclerview.widget.DiffUtil
import com.idleoffice.marctrain.data.model.TrainStatus

object TrainStatusEquality : DiffUtil.ItemCallback<TrainStatus>() {
    override fun areItemsTheSame(oldItem: TrainStatus, newItem: TrainStatus): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TrainStatus, newItem: TrainStatus): Boolean {
        return oldItem == newItem
    }
}