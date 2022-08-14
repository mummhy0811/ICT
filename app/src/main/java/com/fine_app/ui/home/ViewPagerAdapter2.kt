package com.fine_app.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter2 (fragment : FriendRecommendFragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 4
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MatchingAreaFragment()
            1 -> MatchingSchoolFragment()
            2 -> MatchingMajorFragment()
            else -> MatchRandomFragment()
        }
    }
}