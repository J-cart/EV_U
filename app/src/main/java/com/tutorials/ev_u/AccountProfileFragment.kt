package com.tutorials.ev_u

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tutorials.ev_u.arch.EVUViewModel
import com.tutorials.ev_u.databinding.FragmentAccountProfileBinding

class AccountProfileFragment : Fragment() {
    private var _binding: FragmentAccountProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<EVUViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            backBtn.setOnClickListener {
                findNavController().navigateUp()
            }
            Firebase.auth.currentUser?.email?.let {
                authorText.text = it
            } ?: Toast.makeText(requireContext(),"No user at the moment ",Toast.LENGTH_SHORT).show()

            signOutBtn.setOnClickListener {
                viewModel.signOutOp()
                val action =
                    AccountProfileFragmentDirections.actionAccountProfileFragmentToAuthFragment()
                findNavController().navigate(action)
            }
        }
    }


}