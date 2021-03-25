package com.ditto.instructions.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ditto.instructions.domain.model.InstructionModel
import com.ditto.instructions.ui.InstructionFragment
import com.ditto.instructions.ui.InstructionViewModel

/**
 * Created by Sesha on  15/08/2020.
 * Adapter class is to form the tabs dynamically
 */
class TabsAdapter(
    fm: FragmentManager,
    val isFromHome: Boolean,
    val isFromOnboardinScreen: Boolean
) : FragmentStatePagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    private var tabdata = arrayListOf<InstructionModel>()
    private lateinit var viewModel: InstructionViewModel

    fun setListData(listData: List<InstructionModel>) {
        tabdata = listData as ArrayList<InstructionModel>
        notifyDataSetChanged()
    }

    fun setViewModel(vm: InstructionViewModel) {
        viewModel = vm
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return InstructionFragment(
            position,
            isFromHome,
            isFromOnboardinScreen
        )
    }

    override fun getCount(): Int {
        return tabdata.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabdata[position].title
    }
}