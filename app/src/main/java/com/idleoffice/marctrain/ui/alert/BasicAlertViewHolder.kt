package com.idleoffice.marctrain.ui.alert

import androidx.recyclerview.widget.RecyclerView
import com.idleoffice.marctrain.databinding.RecyclerAlertTrainBinding

class BasicAlertViewHolder(
    private val binding: RecyclerAlertTrainBinding,
    private val handleTap: (url: String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(alert: BasicAlert) {
        binding.line.text = alert.line
        binding.description.text = alert.description
        binding.date.text = alert.dateTime

        binding.root.setOnClickListener {
            handleTap(alert.url)
        }
    }

}