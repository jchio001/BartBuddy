package com.app.jonathan.willimissbart.bsa

import android.os.Bundle
import android.view.View
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.application.appComponent
import com.app.jonathan.willimissbart.realtimetrip.RealTimeTripsParentFragment
import com.app.jonathan.willimissbart.store.BsaStore
import com.app.jonathan.willimissbart.utils.view.BaseFragment
import com.app.jonathan.willimissbart.utils.view.changeVisibilityAndEnable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class BsasFragment : BaseFragment(R.layout.fragment_bsa) {

    @Inject lateinit var bsaStore: BsaStore

    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().appComponent.inject(this)

        configureToolbar()

        compositeDisposable.addAll(
            bsaStore.poll()
                .subscribe(),
            bsaStore.stream()
                .subscribe()
        )
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            configureToolbar()
        }
    }

    private fun configureToolbar() {
        (parentFragment as RealTimeTripsParentFragment?)?.also {
            it.editIcon.changeVisibilityAndEnable(false)
            it.title.setText(R.string.notifications)
        }
    }

    override fun onDestroyView() {
        compositeDisposable.dispose()
        super.onDestroyView()
    }

    companion object {

        fun newInstance() = BsasFragment()
    }
}
