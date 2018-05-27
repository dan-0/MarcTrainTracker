package com.idleoffice.marctrain.ui.alert

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.ui.base.BaseViewHolder
import kotlinx.android.synthetic.main.recycler_alert_train.view.*

class AlertAdapter(val alerts: MutableList<TrainAlert>) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : BaseViewHolder {
        val alertLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_alert_train, parent, false) as ConstraintLayout

        return ViewHolder(alertLayout)
    }

    override fun getItemCount() : Int {
        return alerts.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(alertTrain: ConstraintLayout) : BaseViewHolder(alertTrain) {

        private val alertDate = alertTrain.alertDataTextDate
        private val alertDescription = alertTrain.alertDataTextDescription

        override fun onBind(position: Int) {
            val alert = alerts[position]
            alertDate.text = alert.pubDate
            alertDescription.text = alert.description
        }
    }
}