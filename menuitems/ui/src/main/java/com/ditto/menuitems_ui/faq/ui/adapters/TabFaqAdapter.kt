package com.ditto.menuitems_ui.faq.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ditto.menuitems.domain.model.faq.FaqGlossaryResponseDomain
import com.ditto.menuitems_ui.faq.ui.FAQFragment
import com.ditto.menuitems_ui.faq.ui.FAQGlossaryFragmentViewModel
import com.ditto.menuitems_ui.faq.ui.GlossaryFragment
import com.ditto.menuitems_ui.faq.ui.VideosFragment

class TabFaqAdapter(
    fm: FragmentManager,
    var data: FaqGlossaryResponseDomain?
) : FragmentStatePagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    private var tabdata = arrayListOf<String>("FAQ", "Glossary","How-To Videos")
    private lateinit var vm1: FAQGlossaryFragmentViewModel

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                FAQFragment(data?.fAQ?: emptyList())
            }
            1 -> {
                GlossaryFragment(data?.glossary?: emptyList())
            }
            2 -> {
                VideosFragment(data?.videos?: emptyList())
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