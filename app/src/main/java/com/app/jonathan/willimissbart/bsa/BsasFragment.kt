package com.app.jonathan.willimissbart.bsa

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.jonathan.willimissbart.R
import com.app.jonathan.willimissbart.application.appComponent
import com.app.jonathan.willimissbart.realtimetrip.RealTimeTripsParentFragment
import com.app.jonathan.willimissbart.utils.view.BaseFragment
import com.app.jonathan.willimissbart.utils.view.changeVisibilityAndEnable
import com.app.jonathan.willimissbart.utils.view.isVisible
import javax.inject.Inject

class BsasFragment : BaseFragment(R.layout.fragment_bsa) {

    @Inject lateinit var bsasViewModelFactory: BsasViewModelFactory

    private val bsasAdapter = BsasAdapter()

    private val progressBar: ProgressBar by bind(R.id.progress_bar)
    private val recyclerView: RecyclerView by bind(R.id.recycler_view)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().appComponent.inject(this)

        configureToolbar()

        recyclerView.adapter = bsasAdapter
        recyclerView.itemAnimator = null
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val bsasViewModel = ViewModelProviders.of(this, bsasViewModelFactory)
            .get(BsasViewModel::class.java)
        bsasViewModel
            .bsaLiveData
            .observe(viewLifecycleOwner) { viewState ->
                progressBar.isVisible = viewState.showProgressBar
                recyclerView.isVisible = viewState.showRecyclerView
                bsasAdapter.submitList(viewState.bsas)

                if (viewState.unhandledException != null) {
                    throw viewState.unhandledException
                }
            }
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

    companion object {

        fun newInstance() = BsasFragment()
    }
}
