package com.ditto.onboarding.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ditto.onboarding.domain.model.OnboardingData
import com.ditto.onboarding.ui.OnboardingViewModel
import com.ditto.onboarding.ui.R
import com.ditto.onboarding.ui.databinding.OnboardingItemBinding
import core.binding.BindableAdapter


/**
 * Adapter class to map List<OnboardingData> with RecyclerView
 */
class OnboardingAdapter : RecyclerView.Adapter<OnboardingAdapter.OnBoardingHolder>(),
    BindableAdapter<List<OnboardingData>> {

    private val images = intArrayOf(R.drawable.onboard_beam,R.drawable.onboard_calib,R.drawable.onboard_howto,R.drawable.onboarding_demo_video)

    lateinit var viewModel: OnboardingViewModel
    private var onBoarding: List<OnboardingData> = emptyList()
    var mutableList : MutableList<OnboardingData> = mutableListOf()

    override fun setListData(items: List<OnboardingData>) {
        val i = OnboardingData(5,"Take a Tour","Watch the demo video","https://dev02-na03-joann.demandware.net/on/demandware.static/-/Library-Sites-LibrarydittoShared/default/dwb47dc213/mobileTraceImages/onboard_howto.jpg")
        val i1 = OnboardingData(4,"FAQs and Glossary","View Faq and Glossary","https://dev02-na03-joann.demandware.net/on/demandware.static/-/Library-Sites-LibrarydittoShared/default/dwb47dc213/mobileTraceImages/onboard_howto.jpg")
       // items.toMutableList().add(i)
        onBoarding = items
        mutableList = items.toMutableList()
        mutableList!!.add(i1)
        mutableList!!.add(i)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OnboardingItemBinding.inflate(inflater, parent, false)
        return OnBoardingHolder(binding,viewType)
    }

    override fun getItemCount() = mutableList.size

    override fun onBindViewHolder(holder: OnBoardingHolder, position: Int) {
        holder.rowonboardingBinding.onboardingValue = mutableList[position]
        holder.rowonboardingBinding.viewModel = viewModel
        //holder.rowonboardingBinding.imageView.setBackgroundResource(images[position])
        if (position == 4) {
            holder.rowonboardingBinding.imagePlay.visibility = View.VISIBLE
            Glide.with(holder.rowonboardingBinding.cardView.context)
                .load(R.drawable.onboarding_demo_video)
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.rowonboardingBinding.imageView)
        } else {
            Glide.with(holder.rowonboardingBinding.cardView.context)
                .load(mutableList[position].image)
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.rowonboardingBinding.imageView)
        }
    }

    inner class OnBoardingHolder(val rowonboardingBinding: OnboardingItemBinding,viewType: Int) :
        RecyclerView.ViewHolder(rowonboardingBinding.root) {
    }
}

