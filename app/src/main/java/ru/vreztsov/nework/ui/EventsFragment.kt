package ru.vreztsov.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.vreztsov.nework.R
import ru.vreztsov.nework.databinding.FragmentEventsBinding
import ru.vreztsov.nework.util.setBottomNavigationViewListener
import ru.vreztsov.nework.util.setTopAppBarListener
import ru.vreztsov.nework.viewmodel.EventViewModel

class EventsFragment : Fragment() {
    private lateinit var binding: FragmentEventsBinding
    private val viewModel: EventViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsBinding.inflate(inflater, container, false)
        binding.bottomNavigation.selectedItemId = R.id.item_events

        //        val adapter: PostsAdapter = createAdapter()
        subscribe()
        setListeners()
        return binding.root
    }

    private fun subscribe(){

    }

    private fun setListeners(){
        binding.topAppBar.setTopAppBarListener(this, viewModel.isAuthorized)
        binding.bottomNavigation.setBottomNavigationViewListener(this)
    }
}