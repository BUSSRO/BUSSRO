package com.youreye.bussro.feature.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.R
import com.youreye.bussro.model.db.entity.History
import com.youreye.bussro.model.repository.HistoryRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BookmarkFragment : Fragment() {
    private lateinit var rv: RecyclerView
    private lateinit var rvAdapter: HistoryAdapter
    @Inject lateinit var historyRepository: HistoryRepository
    private val historyViewModel by activityViewModels<HistoryViewModel>() // Activity ViewModel 공유

    private lateinit var iv: ImageView
    private lateinit var txt: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVar(view)
        initRecyclerView(view)
        initObserver()
    }

    private fun initVar(view: View) {
        iv = view.findViewById(R.id.iv_bookmark_fragment_off)
        txt = view.findViewById(R.id.txt_bookmark_fragment_off_desc)
    }

    private fun initRecyclerView(view: View) {
        rv = view.findViewById(R.id.rv_bookmark_fragment)
        rvAdapter = HistoryAdapter(requireActivity().supportFragmentManager, requireActivity().application, historyRepository)

        rv.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initObserver() {
        historyViewModel.getBookmarks().observe(viewLifecycleOwner, Observer {
            rvAdapter.updateData(it)
            handleUI(it)
        })
    }

    /* 히스토리가 없는 경우 대응 */
    private fun handleUI(data: List<History>) {
        if (data.isEmpty()) {
            rv.visibility = View.GONE

            iv.visibility = View.VISIBLE
            txt.visibility = View.VISIBLE
        } else {
            rv.visibility = View.VISIBLE

            iv.visibility = View.GONE
            txt.visibility = View.GONE
        }
    }
}