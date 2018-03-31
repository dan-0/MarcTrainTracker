package com.idleoffice.marctrain.ui.status

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.TextView
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.R.id.textLabelDelay
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.ui.base.BaseViewHolder
import timber.log.Timber

class StatusAdapter(val trainStatuses: MutableList<TrainStatus>) : RecyclerView.Adapter<BaseViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val statusLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.status_train, parent, false) as ConstraintLayout

        return ViewHolder(statusLayout)
    }

    override fun getItemCount(): Int {
        return trainStatuses.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
        
    }

    inner class ViewHolder(val statusTrain: ConstraintLayout) : BaseViewHolder(statusTrain) {

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

        private val trainNumber : TextView = statusTrain.findViewById(R.id.trainNumber)
        private val trainNextStation : TextView = statusTrain.findViewById(R.id.textDataStationName)
        private val trainDepart : TextView = statusTrain.findViewById(R.id.textDataDepart)
        private val trainStatus : TextView = statusTrain.findViewById(R.id.textDataStatus)
        private val trainDelay : TextView? = statusTrain.findViewById(R.id.textDataDelay)
        private val trainDelayLabel : TextView? = statusTrain.findViewById(R.id.textLabelDelay)
    }
}