package com.idleoffice.marctrain.ui.status

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.databinding.CardStatusTrainBinding
import timber.log.Timber

class TrainStatusViewHolder(
    private val binding: CardStatusTrainBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(status: TrainStatus) {
        Timber.d("binding $status")

        with(binding) {
            trainNumber.text = status.number
            textDataStationName.text = status.nextStation
            textDataDepart.text = status.departure
            textDataStatus.text = status.status

            if (status.delay.isEmpty()) {
                Timber.d("Removing delay")
                textDataDelay.visibility = View.GONE
                textLabelDelay.visibility = View.GONE
            }

            textDataDelay.text = status.delay
        }
    }
}