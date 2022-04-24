package com.camelcc.overcooked

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var photosView: RecyclerView
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh = view.findViewById(R.id.refresh)
        photosView = view.findViewById(R.id.photos)

        refresh.setOnRefreshListener {
            viewModel.refresh()
        }

        photosView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        photosView.layoutManager = layoutManager
        adapter = PhotoAdapter(onItemClicked = { photo ->
            viewModel.selectPhoto(photo)
            findNavController().navigate(R.id.action_mainFragment_to_detailFragment)
        }, onToggleClicked = { photo ->
            viewModel.togglePhotoButton(photo)
        })
        adapter.setHasStableIds(true)
        photosView.adapter = adapter

        photosView.setOnScrollChangeListener { _, _, _, _, _ ->
            updateVisible()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadingFlow.collect {
                    refresh.isRefreshing = it == LoadingState.Loading
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.photosFlow.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun updateVisible() {
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()
        if (first >= 0 && last >= 0 && first <= last) {
            viewModel.updateVisiblePhotos(adapter.visiblePhotos(first, last))
        }
    }
}