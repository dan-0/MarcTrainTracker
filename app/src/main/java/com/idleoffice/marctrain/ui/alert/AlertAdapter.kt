package com.idleoffice.marctrain.ui.alert

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.ui.base.BaseViewHolder
import kotlinx.android.synthetic.main.recycler_alert_train.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AlertAdapter(val alerts: MutableList<TrainAlert>) : RecyclerView.Adapter<BaseViewHolder>() {

    val fromDateFormatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault())

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

    inner class ViewHolder(private val alertTrain: ConstraintLayout) : BaseViewHolder(alertTrain) {

        private val alertDate = alertTrain.alertDataTextDate
        private val alertDescription = alertTrain.alertDataTextDescription

        override fun onBind(position: Int) {
            val alert = alerts[position]

            val pubDate = try {
                val d = fromDateFormatter.parse(alert.pubDate)
                DateUtils.formatDateTime(alertTrain.context, d.time,
                        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_ABBREV_MONTH)
            } catch (e: ParseException) {
                alert.pubDate
            }

            alertDate.text = pubDate
            alertDescription.text = alert.description
        }
    }
}