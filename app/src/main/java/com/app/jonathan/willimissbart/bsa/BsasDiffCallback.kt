package com.app.jonathan.willimissbart.bsa

import androidx.recyclerview.widget.DiffUtil
import com.app.jonathanchiou.willimissbart.api.models.bsa.ApiBsa

class BsasDiffCallback : DiffUtil.ItemCallback<ApiBsa>() {

    override fun areItemsTheSame(oldItem: ApiBsa, newItem: ApiBsa) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ApiBsa, newItem: ApiBsa) =
        oldItem.equals(newItem)
}
