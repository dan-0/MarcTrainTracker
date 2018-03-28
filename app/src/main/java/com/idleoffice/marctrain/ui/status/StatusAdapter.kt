package com.idleoffice.marctrain.ui.status

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainStatus
import timber.log.Timber

class StatusAdapter(private val trainStatuses: List<TrainStatus>) : RecyclerView.Adapter<StatusAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusAdapter.ViewHolder {
        val statusLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.status_train, parent, false) as ConstraintLayout

        return ViewHolder(statusLayout)
    }

    override fun getItemCount(): Int {
        return trainStatuses.size
    }

    override fun onBindViewHolder(holder: StatusAdapter.ViewHolder, position: Int) {
        val st = trainStatuses[position]
        Timber.d("binding $st")
        holder.trainNumber?.text = st.number
        holder.trainNextStation?.text = st.nextStation
        holder.trainDepart?.text = st.departure
        holder.trainStatus?.text = st.status
        holder.trainDelay?.text = st.delay
//        holder.trainMessage?.text = st.message
        
    }

    class ViewHolder(statusTrain: ConstraintLayout) : RecyclerView.ViewHolder(statusTrain) {
        var trainNumber : TextView?
        var trainNextStation : TextView?
        var trainDepart : TextView?
        var trainStatus : TextView?
        var trainDelay : TextView?
//        var trainMessage : TextView?

        init {
            trainNumber = statusTrain.findViewById(R.id.trainNumber)
            trainNextStation = statusTrain.findViewById(R.id.textDataStationName)
            trainDepart = statusTrain.findViewById(R.id.textDataDepart)
            trainStatus = statusTrain.findViewById(R.id.textDataStatus)
            trainDelay = statusTrain.findViewById(R.id.textDataDelay)
        }
    }
}