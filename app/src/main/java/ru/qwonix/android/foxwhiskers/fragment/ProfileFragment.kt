package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentProfileBinding
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.viewmodel.AuthenticationViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.TokenViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val TAG = "ProfileFragment"

    private lateinit var binding: FragmentProfileBinding
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()
    private val tokenViewModel: TokenViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authenticationViewModel.authenticatedUser.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "${it.code} — ${it.errorMessage}")
                    findNavController().navigate(R.id.action_profileFragment_to_phoneNumberInputFragment)
                }

                ApiResponse.Loading -> {
                    "Loading"
                }

                is ApiResponse.Success -> {
                    Log.i(TAG, "Success profile load ${it.data}")

                    binding.userProfile = it.data

                    if (it.data != null && authenticationViewModel.isRequiredForEdit(it.data)) {
                        findNavController().navigate(
                            ProfileFragmentDirections.actionProfileFragmentToProfileEditingFragment(
                                it.data
                            )
                        )
                    }
                }
            }
        }

        tokenViewModel.token.observe(viewLifecycleOwner) { token ->
//            if (token == null)
//                findNavController().navigate(R.id.action_profileFragment_to_phoneNumberInputFragment)
        }



        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                authenticationViewModel.logout()
            }
        }

        binding.editButton.setOnClickListener {
            val apiResponse = authenticationViewModel.authenticatedUser.value
            if (apiResponse is ApiResponse.Success) {
                if (apiResponse.data != null && authenticationViewModel.isRequiredForEdit(
                        apiResponse.data
                    )
                ) {
                    findNavController().navigate(
                        ProfileFragmentDirections.actionProfileFragmentToProfileEditingFragment(
                            apiResponse.data
                        )
                    )
                }
            }
        }
    }
}