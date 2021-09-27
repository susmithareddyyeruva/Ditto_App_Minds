package com.ditto.mylibrary.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ditto.mylibrary.ui.AllPatternsFragment

class MyLibraryAdapter(supportFragmentManager: FragmentManager) : FragmentStatePagerAdapter(supportFragmentManager) {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList.get(position)
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    fun addFragment(fragment: Fragment, title: String,listener: AllPatternsFragment.SetPatternCount,isfilter:AllPatternsFragment.FilterIconSetListener,
    ) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }
}