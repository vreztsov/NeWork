package ru.vreztsov.nework.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.vreztsov.nework.R
import ru.vreztsov.nework.auth.LoginInfo
import ru.vreztsov.nework.databinding.FragmentLoginBinding
import ru.vreztsov.nework.util.AndroidUtils
import ru.vreztsov.nework.util.AndroidUtils.showToast
import ru.vreztsov.nework.util.goToUser
import ru.vreztsov.nework.viewmodel.LoginViewModel

@AndroidEntryPoint
class LoginFragment : Fragment() {

    val viewModel by activityViewModels<LoginViewModel>()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        resetErrors()
        binding.loginButton.isEnabled = true
        subscribe()
        setListeners()
        return binding.root
    }

    private fun resetErrors(){
        viewModel.resetErrors()
    }

    private fun subscribe() {
        subscribeErrorMessages(
            hashMapOf(
                Pair(viewModel.loginErrorMessage, binding.layoutLoginTextField),
                Pair(viewModel.passwordErrorMessage, binding.layoutPasswordTextField),
            )
        )

        viewModel.loginSuccessEvent.observe(viewLifecycleOwner) {
            AndroidUtils.hideKeyboard(requireView())
            val navController = findNavController()
            val navUp = navController.navigateUp()
            if (!navUp){
                goToUser(this, viewModel.profileId)
            }
            Log.i(
                "LoginFragment was leaved",
                if (navUp) "navigating up" else "navigating to profile"
            )
        }
        viewModel.loginError.observe(viewLifecycleOwner) { errText ->
            if (errText == null) return@observe
            showToast(activity, errText)
            binding.loginButton.isEnabled = true
        }
    }

    private fun setListeners() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.loginToRegisterButton.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            Log.i("LoginFragment", "LoginFragment was leaved, navigating to RegisterFragment")
        }

        binding.loginButton.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            viewModel.resetLoginInfo(
                LoginInfo(
                    binding.loginTextField.text.toString(),
                    binding.passwordTextField.text.toString()
                )
            )
            if (viewModel.completed()){
                binding.loginButton.isEnabled = false
                viewModel.doLogin()
            }
        }
    }

    private fun subscribeErrorMessages(
        map: Map<LiveData<Int?>, TextInputLayout>
    ) {
        map.keys.forEach { liveData ->
            liveData.observe(viewLifecycleOwner) { resId ->
                Log.d("LoginFragment", "Login info: ${viewModel.loginInfo}")
                map.getValue(liveData).error = resId?.let {
                    getString(it)
                }
            }
        }
    }
}