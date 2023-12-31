package com.ditto.howto.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ditto.howto.fragment.TabContentFragment
import com.ditto.howto.model.HowToModel
import com.ditto.howto.ui.HowtoViewModel

/**
 * Created by Sesha on  15/08/2020.
 * Adapter class is to form the tabs dynamically
 */
class TabsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var tabdata = arrayListOf<HowToModel>()
    private lateinit var vm1: HowtoViewModel

    fun setListData(listData: List<HowToModel>) {
        tabdata = listData as ArrayList<HowToModel>
//        //for sewing
//        tabdata.removeAt(3)
//        //for cutting mat
//        tabdata.removeAt(3)

        notifyDataSetChanged()
    }

    fun setViewModel(vm: HowtoViewModel) {
        vm1 = vm
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return TabContentFragment().apply {
            vm = vm1
            pos = position
        }
    }

    override fun getCount(): Int {
        return tabdata?.size ?:0
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabdata?.get(position)?.title ?:""
    }
}