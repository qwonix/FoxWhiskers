package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentProfileBinding
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler
import ru.qwonix.android.foxwhiskers.viewmodel.ProfileViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val TAG = "ProfileFragment"

    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        Log.i(TAG, "onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.tryLoadUserProfile(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })

        profileViewModel.authenticatedUser.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                    findNavController().navigate(R.id.action_profileFragment_to_phoneNumberInputFragment)
                }

                is ApiResponse.Loading -> {
                    Log.i(TAG, "loading")
                }

                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful profile load – ${it.data}")
                    val data: UserProfile = it.data!!

                    binding.userProfile = data

                    if (profileViewModel.isRequiredForEdit(data)) {
                        findNavController().navigate(
                            ProfileFragmentDirections.actionProfileFragmentToProfileEditingFragment(
                                data
                            )
                        )
                    }
                }
            }
        }


        binding.logoutButton.setOnClickListener {
            Log.i(TAG, "logout – ${binding.userProfile}")
            profileViewModel.logout(object : CoroutinesErrorHandler {
                override fun onError(message: String) {
                    TODO("Not yet implemented")
                }
            })
        }

        binding.editButton.setOnClickListener {
            Log.i(TAG, "edit – ${binding.userProfile}")
            val userProfile = binding.userProfile

            if (userProfile != null && profileViewModel.isRequiredForEdit(userProfile)) {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToProfileEditingFragment(
                        userProfile
                    )
                )
            }
        }
    }
}