package com.tutorials.ev_u

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tutorials.ev_u.util.RequestState
import com.tutorials.ev_u.databinding.FragmentAuthBinding
import com.tutorials.ev_u.arch.EVUViewModel
import com.tutorials.ev_u.util.EV_U_TAG
import kotlinx.coroutines.launch


class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<EVUViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Firebase.auth.currentUser != null){
            val action = AuthFragmentDirections.actionAuthFragmentToHomeFragment()
            findNavController().navigate(action)
            return
        }

        lifecycleScope.launch {
            viewModel.authState.collect {
                if (it) {
                    binding.loginLayouts.root.isVisible = true
                    binding.signUpLayouts.root.isVisible = false
                    bindLogin()
                } else {
                    binding.loginLayouts.root.isVisible = false
                    binding.signUpLayouts.root.isVisible = true
                    bindSignUp()
                }

            }
        }

    }

    private fun bindSignUp(){
        binding.signUpLayouts.apply {
            backBtn.setOnClickListener {
                viewModel.toggleAuthState(true)
            }

            createAccBtn.setOnClickListener {

                val email = emailEdt.text.toString()
                val password = passwordEdt.text.toString()
                val fName = fNameEdt.text.toString()
                val lName = lNameEdt.text.toString()

                if (email.trim().isEmpty()) {
                    Toast.makeText(requireContext(), "Email field is empty", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                if (password.trim().isEmpty()) {
                    Toast.makeText(requireContext(), "Password field is empty", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                if (!email.trim().contains("@gmail.com")) {
                    Toast.makeText(
                        requireContext(),
                        "Please input a valid email address '@gmail.com'",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (!binding.signUpLayouts.acceptCheckbox.isChecked) {
                    Toast.makeText(requireContext(), "Accept the T&C", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                showDialog(false) {
                    viewModel.signUp(fName, lName, email, password)
                }
            }
        }
        observeSignUpState()
    }

    private fun bindLogin() {
        binding.loginLayouts.apply {

            createText.setOnClickListener {
                viewModel.toggleAuthState(false)
            }


            loginBtn.setOnClickListener {

                val email = emailEdt.text.toString()
                val password = passwordEdt.text.toString()

                if (email.trim().isEmpty()) {
                    Toast.makeText(requireContext(), "Email field is empty", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                if (password.trim().isEmpty()) {
                    Toast.makeText(requireContext(), "Password field is empty", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                viewModel.login(email, password)
                observeLoginState()
            }

        }
    }


    private fun showDialog(state: Boolean, action: () -> Unit) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
        if (state) {
            dialog.setTitle("Verification Notice")
            dialog.setMessage("A verification link has been sent to your email, please verify your email to complete your profile.")
            dialog.setCancelable(false)
        } else {
            dialog.setTitle("Information Notice")
            dialog.setMessage("Please confirm your email is valid and active before proceeding as a verification link will be sent to it.")
            dialog.setCancelable(true)
        }
        dialog.setNegativeButton("Dismiss") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        dialog.setPositiveButton("Proceed") { dialogInterface, _ ->
            action()
            dialog.setCancelable(true)
            dialogInterface.dismiss()
        }
        dialog.show()

    }

    private fun observeLoginState(){
        lifecycleScope.launch {
            viewModel.loginState.collect {
                binding.loginLayouts.progressBar.isVisible = it is RequestState.Loading
                when (it) {
                    is RequestState.Successful -> {
                        Toast.makeText(
                            requireContext(),
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(EV_U_TAG, "login state-> ${it.data}")
                        viewModel.toggleLoginState(RequestState.NonExistent)
                        val action =
                            AuthFragmentDirections.actionAuthFragmentToHomeFragment()
                        findNavController().navigate(action)
                    }
                    is RequestState.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            "Login Failed: ${it.msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.toggleLoginState(RequestState.NonExistent)
                        Log.d(EV_U_TAG, "login state error-> ${it.msg}")
                    }
                    else -> Unit
                }
            }
        }

    }
    private fun observeSignUpState(){
        lifecycleScope.launch {
            viewModel.signUpState.collect {
                binding.signUpLayouts.progressBar.isVisible = it is RequestState.Loading
                when (it) {
                    is RequestState.Successful -> {
                        Toast.makeText(
                            requireContext(),
                            "Sign up Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(EV_U_TAG, "bindSignUp: signup state-> ${it.data}")
                        showDialog(true) {
                            viewModel.toggleSignUpState(RequestState.NonExistent)
                            val action =
                                AuthFragmentDirections.actionAuthFragmentToHomeFragment()
                            findNavController().navigate(action)
                        }
                    }
                    is RequestState.Failure -> {
                        viewModel.toggleSignUpState(RequestState.NonExistent)
                        Toast.makeText(
                            requireContext(),
                            "Sign up Failed: ${it.msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(EV_U_TAG, "bindSignUp: signup state error-> ${it.msg}")
                    }
                    else -> Unit
                }
            }
        }

    }


}