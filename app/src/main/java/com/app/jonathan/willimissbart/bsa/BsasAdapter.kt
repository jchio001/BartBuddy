package com.app.jonathan.willimissbart.bsa

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.jonathan.willimissbart.utils.view.GenericViewHolder
import com.app.jonathanchiou.willimissbart.db.models.Bsa

class BsasAdapter : ListAdapter<Bsa, GenericViewHolder>(
    BsasDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        return GenericViewHolder(BsaView.inflate(parent))
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        @Suppress("UNCHECKED_CAST")
        (holder.itemView as BsaView).also { bsaView ->
            bsaView.accept(getItem(position))
        }
    }
}
