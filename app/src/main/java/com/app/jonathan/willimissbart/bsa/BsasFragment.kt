package com.app.jonathan.willimissbart.bsa

import android.os.Bundle
import android.view.View
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.application.appComponent
import com.app.jonathan.willimissbart.store.BsaStore
import com.app.jonathan.willimissbart.utils.view.BaseFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BsasFragment : BaseFragment(R.layout.fragment_bsa) {

    @Inject lateinit var bsaStore: BsaStore

    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().appComponent.inject(this)

        compositeDisposable.add(
            bsaStore.getBsas()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    override fun onDestroyView() {
        compositeDisposable.dispose()
        super.onDestroyView()
    }

    companion object {

        fun newInstance() = BsasFragment()
    }
}
