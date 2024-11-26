package ru.vreztsov.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.vreztsov.nework.databinding.FragmentNewJobBinding

class NewJobFragment: Fragment() {


    private lateinit var binding: FragmentNewJobBinding
    private val datePicker = DatePickerFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewJobBinding.inflate(inflater, container, false)

        binding.dateChip.setOnClickListener {
            datePicker.show(parentFragmentManager, "datePicker")
        }

        return binding.root
    }

}