package com.ditto.menuitems_ui.faq.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ditto.menuitems.domain.model.faq.FaqGlossaryResponseDomain
import com.ditto.menuitems_ui.faq.ui.FAQFragment
import com.ditto.menuitems_ui.faq.ui.FAQGlossaryfragmentViewModel
import com.ditto.menuitems_ui.faq.ui.GlossaryFragment

class TabFaqAdapter(
    fm: FragmentManager,
    var data: FaqGlossaryResponseDomain?
) : FragmentStatePagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    private var tabdata = arrayListOf<String>("FAQ", "Glossary","Videos")
    private lateinit var vm1: FAQGlossaryfragmentViewModel

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                FAQFragment(data?.FAQ?: emptyList())
            }
            1 -> {
                GlossaryFragment(data?.Glossary?: emptyList())
            }
            2 -> {
                FAQFragment(data?.FAQ?: emptyList())
            }
            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return tabdata.size
    }


    override fun getPageTitle(position: Int): CharSequence {
        return tabdata[position]
    }
    fun setMainData(data: FaqGlossaryResponseDomain?){
        this.data=data
        notifyDataSetChanged()
    }


}