package com.ditto.onboarding.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

    private val images = intArrayOf(R.drawable.onboard_beam,R.drawable.onboard_calib,R.drawable.onboard_howto)

    lateinit var viewModel: OnboardingViewModel
    private var onBoarding: List<OnboardingData> = emptyList()

    override fun setListData(items: List<OnboardingData>) {
        onBoarding = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OnboardingItemBinding.inflate(inflater, parent, false)
        return OnBoardingHolder(binding,viewType)
    }

    override fun getItemCount() = onBoarding.size

    override fun onBindViewHolder(holder: OnBoardingHolder, position: Int) {
        holder.rowonboardingBinding.onboardingValue = onBoarding[position]
        holder.rowonboardingBinding.viewModel = viewModel
        holder.rowonboardingBinding.imageView.setBackgroundResource(images[position])
       /* Glide.with(holder.rowonboardingBinding.cardView.context)
            .load(onBoarding[position].image)
            .placeholder(com.ditto.howto_ui.R.drawable.ic_placeholder)
            .into(holder.rowonboardingBinding.imageView)*/
    }

    inner class OnBoardingHolder(val rowonboardingBinding: OnboardingItemBinding,viewType: Int) :
        RecyclerView.ViewHolder(rowonboardingBinding.root) {
    }
}

