package ru.vreztsov.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.core.util.Pair
import androidx.core.widget.addTextChangedListener
import com.google.android.material.datepicker.MaterialDatePicker
import ru.vreztsov.nework.R
import ru.vreztsov.nework.databinding.FragmentEditJobBinding
import ru.vreztsov.nework.util.AndroidUtils
import ru.vreztsov.nework.util.BundleArguments.Companion.job
import ru.vreztsov.nework.util.BundleArguments.Companion.userId
import ru.vreztsov.nework.util.DataViewTransform
import ru.vreztsov.nework.util.EditType
import ru.vreztsov.nework.viewmodel.JobViewModel

class EditJobFragment : Fragment() {

    private val jobViewModel: JobViewModel by activityViewModels()
    private lateinit var binding: FragmentEditJobBinding
    private lateinit var editType: EditType
    private lateinit var dateRangePicker: MaterialDatePicker<Pair<Long, Long>>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditJobBinding.inflate(inflater, container, false)
        with(binding) {
            arguments?.userId?.let { _ ->
                editType = EditType.NEW_JOB
                createButton.text = getString(R.string.create_job)
            }
            arguments?.job?.let { job ->
                editType = EditType.EDIT_JOB
                createButton.text = getString(R.string.save_changes)
                nameTextField.setText(job.name)
                positionTextField.setText(job.position)
                linkTextField.setText(job.link)
                dateChip.text = DataViewTransform.periodToString(job)
            }
            if (!this@EditJobFragment::editType.isInitialized) return binding.root
            topAppBar.title = editType.title
        }
        setListeners()
        checkFieldsFilled()
        return binding.root
    }

    private fun setListeners() {
        with(binding) {
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            nameTextField.addTextChangedListener {
                checkFieldsFilled()
            }
            positionTextField.addTextChangedListener {
                checkFieldsFilled()
            }
            dateChip.addTextChangedListener {
                checkFieldsFilled()
            }
            dateChip.setOnClickListener {
                dateRangePicker =
                    MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText(R.string.select_date)
                        .setTheme(R.style.ThemeOverlay_Material3_MaterialCalendar_NeWork)
                        .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                        .apply {
                            with(jobViewModel) {
                                if (startDate != null) {
                                    if (finishDate != null) {
                                        setSelection(Pair(startDate, finishDate))
                                    } else {
                                        setSelection(Pair(startDate, startDate))
                                    }
                                }
                            }
                        }
                        .build()
                dateRangePicker.show(parentFragmentManager, getString(R.string.select_date))
                dateRangePicker.addOnPositiveButtonClickListener {
                    jobViewModel.startDate = it.first
                    if (it.first == it.second) {
                        dateChip.text = DataViewTransform.periodToString(it.first, null)
                        jobViewModel.finishDate = null
                    } else {
                        dateChip.text = DataViewTransform.periodToString(it.first, it.second)
                        jobViewModel.finishDate = it.second
                    }
                }
            }
            createButton.setOnClickListener {
                val dateSelection = dateRangePicker.selection ?: return@setOnClickListener
                jobViewModel.changeContent(
                    nameTextField.text.toString(),
                    positionTextField.text.toString(),
                    dateSelection.first,
                    dateSelection.second,
                    linkTextField.text.toString()
                )
                jobViewModel.save()
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }
        }
    }

    private fun checkFieldsFilled() {
        with(binding) {
            createButton.isEnabled =
                !nameTextField.text.isNullOrBlank() &&
                        !positionTextField.text.isNullOrBlank() &&
                        !dateChip.text.equals(getString(R.string.enter_dates))
        }


    }
}