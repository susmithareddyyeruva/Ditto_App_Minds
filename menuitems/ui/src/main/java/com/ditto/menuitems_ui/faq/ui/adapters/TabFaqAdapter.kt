package com.ditto.menuitems_ui.faq.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ditto.menuitems_ui.faq.ui.FAQFragment
import com.ditto.menuitems_ui.faq.ui.GlossaryFragment

/**
 * Created by Sesha on  15/08/2020.
 * Adapter class is to form the tabs dynamically
 */
class TabFaqAdapter(
    fm: FragmentManager
) : FragmentStatePagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    private var tabdata = arrayListOf<String>("FAQ", "Glossary")

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                FAQFragment()
            }
            1 -> {
                GlossaryFragment()
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
}