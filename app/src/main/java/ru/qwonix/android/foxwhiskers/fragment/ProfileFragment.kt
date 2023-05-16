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
import ru.qwonix.android.foxwhiskers.entity.Client
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

        profileViewModel.tryLoadClient(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })

        profileViewModel.clientAuthenticationResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                    findNavController().navigate(R.id.action_profileFragment_to_phoneNumberInputFragment)
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")

                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful client load – ${it.data}")

                    if (it.data != null) {
                        if (profileViewModel.isRequiredForEdit(it.data)) {
                            findNavController().navigate(
                                ProfileFragmentDirections.actionProfileFragmentToProfileEditingFragment(
                                    it.data
                                )
                            )
                        }
                        binding.client = it.data
                    }

                }
            }
        }


        binding.logoutButton.setOnClickListener {
            Log.i(TAG, "logout – ${binding.client}")
            profileViewModel.logout(object : CoroutinesErrorHandler {
                override fun onError(message: String) {
                    TODO("Not yet implemented")
                }
            })
            findNavController().navigate(R.id.action_profileFragment_to_phoneNumberInputFragment)
        }

        binding.editButton.setOnClickListener {
            Log.i(TAG, "edit – ${binding.client}")
            val client: Client = binding.client!!

            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToProfileEditingFragment(
                    client
                )
            )
        }
    }
}