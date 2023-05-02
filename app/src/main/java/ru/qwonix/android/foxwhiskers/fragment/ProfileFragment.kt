package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
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
import ru.qwonix.android.foxwhiskers.viewmodel.UserProfileViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val userProfileViewModel: UserProfileViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userProfileViewModel.loggedUserProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile == null) {
                findNavController().navigate(R.id.action_profileFragment_to_phoneNumberInputFragment)
            } else {
                binding.userProfile = userProfile

                if (userProfile.isRequiredForEdit()) {
                    findNavController().navigate(
                        ProfileFragmentDirections.actionProfileFragmentToProfileEditingFragment(
                            userProfile
                        )
                    )
                }

                binding.logoutButton.setOnClickListener {
                    lifecycleScope.launch {
                        userProfileViewModel.logout()
                    }
                }

                binding.editButton.setOnClickListener {
                    findNavController().navigate(
                        ProfileFragmentDirections.actionProfileFragmentToProfileEditingFragment(
                            userProfile
                        )
                    )
                }
            }
        }
    }
}