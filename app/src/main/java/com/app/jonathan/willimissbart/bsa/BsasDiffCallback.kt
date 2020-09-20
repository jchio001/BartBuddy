package com.app.jonathan.willimissbart.bsa

import androidx.recyclerview.widget.DiffUtil
import com.app.jonathanchiou.willimissbart.db.models.Bsa

class BsasDiffCallback : DiffUtil.ItemCallback<Bsa>() {

    override fun areItemsTheSame(oldItem: Bsa, newItem: Bsa) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Bsa, newItem: Bsa) =
        oldItem.equals(newItem)
}
