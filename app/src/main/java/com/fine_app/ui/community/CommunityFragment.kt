package com.fine_app.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.fine_app.R
import com.fine_app.databinding.FragmentCommunityBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewPager: ViewPager2 = binding.pager
        val tabLayout: TabLayout = binding.tabLayout
        val viewpagerFragmentAdapter = ViewPagerAdapter(this)

        viewPager.adapter = viewpagerFragmentAdapter

        val tabTitles = listOf("일반", "그룹")

        TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = tabTitles[position] }.attach()

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            //검색 버튼 눌렀을 때 호출
            override fun onQueryTextSubmit(p0: String): Boolean {
                val bundle= bundleOf("text" to p0)
                findNavController().navigate(R.id.action_navigation_community_to_navigation_communitySearch, bundle)

                binding.searchView.setQuery(null, false)
                return true
            }
            //텍스트 입력과 동시에 호출
            override fun onQueryTextChange(p0: String): Boolean {
                return true
            }
        })
        val writingButton=binding.floatingActionButton
        writingButton.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_community_to_postingFragment)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

