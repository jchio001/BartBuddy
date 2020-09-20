package com.app.jonathan.willimissbart.bsa

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.jonathan.willimissbart.store.BsaStore
import io.reactivex.disposables.CompositeDisposable

class BsasViewModel(
    bsaStore: BsaStore
) : ViewModel() {

    val bsaLiveData = MutableLiveData<BsaViewState>()

    private val compositeDisposable = CompositeDisposable()

    init {
        bsaLiveData.postValue(
            BsaViewState(
                showProgressBar = true,
                showRecyclerView = false,
                throwable = null
            )
        )

        compositeDisposable.addAll(
            bsaStore
                .poll()
                .subscribe(),
            bsaStore
                .stream()
                .subscribe(
                    { bsas ->
                        bsaLiveData.postValue(
                            BsaViewState(
                                showProgressBar = false,
                                showRecyclerView = true,
                                bsas = bsas,
                                throwable = null
                            )
                        )
                    },
                    { throwable ->
                        bsaLiveData.postValue(
                            BsaViewState(
                                showProgressBar = false,
                                showRecyclerView = false,
                                throwable = throwable
                            )
                        )
                    }
                )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
