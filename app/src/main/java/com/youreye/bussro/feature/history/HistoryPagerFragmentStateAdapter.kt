package com.youreye.bussro.feature.history

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HistoryPagerFragmentStateAdapter(fragmentActivity: FragmentActivity)
    :FragmentStateAdapter(fragmentActivity) {
    private var fragments = arrayListOf<Fragment>()

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
        notifyItemInserted(fragments.size - 1)
    }

    fun removeFragment() {
        fragments.removeAt(fragments.size - 1)
        notifyItemRemoved(fragments.size - 1)
    }
}