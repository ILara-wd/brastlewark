package com.warriorsdev.brastlewark

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout.LayoutParams
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.warriorsdev.brastlewark.databinding.DialogFiltersBinding
import com.warriorsdev.brastlewark.utils.asInt
import com.warriorsdev.brastlewark.utils.di.Injector
import com.warriorsdev.brastlewark.viewmodel.GnomeListViewModel

/**
 * Dialog class that displays filters to apply for gnomes searches.
 */
class FiltersAlertDialog : DialogFragment() {
    private lateinit var binding: DialogFiltersBinding
    private lateinit var viewModel: GnomeListViewModel
    private lateinit var factory: GnomeListViewModel.Factory

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?) =
        DialogFiltersBinding.inflate(inflater, parent, false).also {
            binding = it
            binding.closeButton.setOnClickListener { dismissAllowingStateLoss() }
            factory = Injector.getInstance(requireActivity().application)
                .provideGnomeListViewModelFactory()
            viewModel =
                ViewModelProvider(requireActivity(), factory)[GnomeListViewModel::class.java]
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRangedSettings()
        setupMultipleOptionSettings()
        setupApplyFiltersButton()
        view.setBackgroundColor(Color.BLUE)
    }

    private fun setupRangedSettings() = binding.run {
        getRangeSettings(viewModel.getAgeSettings(), ageFrom, ageTo)
        getRangeSettings(viewModel.getWeightSettings(), weightFrom, weightTo)
        getRangeSettings(viewModel.getHeightSettings(), heightFrom, heightTo)
        getRangeSettings(viewModel.getFriendsSettings(), friendsFrom, friendsTo)
    }

    private fun setupMultipleOptionSettings() {
        setupHairColorOptions()
        setupProfessionOptions()
    }

    /**
     * Retrieves the range settings for views.
     */
    private fun getRangeSettings(range: LiveData<IntRange>, minView: EditText, maxView: EditText) =
        range.observe(this, Observer {
            minView.setText(it.first.toString())
            maxView.setText(it.last.toString())
        })

    /**
     * Setups the hair color options.
     */
    private fun setupHairColorOptions() = viewModel.hairColors.sortedBy { it }.forEach {
        createCheckbox(it, binding.hairCheckBoxGroup)
    }

    /**
     * Setups the professions as options.
     */
    private fun setupProfessionOptions() =
        viewModel.professions.map { it.replace(" T", "T") }.sortedBy { it }.forEach {
            createCheckbox(it, binding.professionsCheckBoxGroup)
        }

    /**
     * Creates a checkbox elements.
     */
    private fun createCheckbox(text: String, parent: ViewGroup) {
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val checkBox = CheckBox(context).apply {
            tag = text
            setText(text)
            setTextColor(Color.WHITE)
            buttonTintList = ContextCompat.getColorStateList(context, R.color.checkbox_state_list)
        }
        parent.addView(checkBox, params)
    }

    /**
     * Retrieves selected options from multiple options selections.
     */
    private fun getMultipleSelectedOptions(parent: ViewGroup): Set<String> {
        var options = setOf<String>()
        parent.forEach {
            val checkBox = it as? CheckBox
            val tag = checkBox?.tag as? String
            if (checkBox?.isChecked == true && !tag.isNullOrBlank()) {
                options = options.plus(tag)
            }
        }
        return options
    }

    /**
     * Retrieves IntRange objects from the views that allows minimum and maximum values.
     */
    private fun getRangeFromViews(minView: EditText, maxView: EditText) =
        minView.text.asInt()..maxView.text.asInt()

    /**
     * Sets the click listener that will apply the given filters to the gnomes list.
     */
    private fun setupApplyFiltersButton() = binding.run {
        searchButton.setOnClickListener {
            viewModel.filterGnomes(
                getRangeFromViews(ageFrom, ageTo),
                getRangeFromViews(heightFrom, heightTo),
                getRangeFromViews(weightFrom, weightTo),
                getRangeFromViews(friendsFrom, friendsTo),
                getMultipleSelectedOptions(hairCheckBoxGroup),
                getMultipleSelectedOptions(professionsCheckBoxGroup)
            )
            dismissAllowingStateLoss()
        }
    }
}