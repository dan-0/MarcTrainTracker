package com.idleoffice.marctrain.ui.alert

import androidx.recyclerview.widget.DiffUtil

object AlertEquality : DiffUtil.ItemCallback<BasicAlert>() {
    override fun areItemsTheSame(oldItem: BasicAlert, newItem: BasicAlert): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BasicAlert, newItem: BasicAlert): Boolean {
        return oldItem == newItem
    }
}