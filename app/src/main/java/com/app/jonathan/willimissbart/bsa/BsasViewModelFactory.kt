package com.app.jonathan.willimissbart.bsa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.jonathan.willimissbart.store.BsaStore
import javax.inject.Inject

class BsasViewModelFactory @Inject constructor(
    private val bsaStore: BsaStore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == BsasViewModel::class.java) {
            return BsasViewModel(bsaStore) as T
        }

        throw IllegalStateException("Invalid class!")
    }
}
