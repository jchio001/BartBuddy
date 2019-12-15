package com.app.jonathan.willimissbart.utils

import androidx.recyclerview.widget.DiffUtil

class BasicDiffCallback<T> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = (oldItem === newItem)

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = (oldItem === newItem)
}
