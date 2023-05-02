package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentProfileEditingBinding
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.util.EditTextState
import ru.qwonix.android.foxwhiskers.viewmodel.UserProfileViewModel

class ProfileEditingFragment : Fragment(R.layout.fragment_profile_editing) {

    private val profileViewModel: UserProfileViewModel by activityViewModels()

    private val args: ProfileEditingFragmentArgs by navArgs()
    private lateinit var changedUserProfile: UserProfile

    private lateinit var binding: FragmentProfileEditingBinding

    companion object {

        @JvmStatic
        @BindingAdapter("state")
        fun setBackgroundStyle(editText: EditText, editTextState: EditTextState) {
            editText.setBackgroundResource(editTextState.backgroundId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEditingBinding.inflate(inflater, container, false)

        this.changedUserProfile = args.userProfile
        binding.userProfile = changedUserProfile

        binding.firstnameFieldState = EditTextState.IN_PROGRESS
        binding.lastnameFieldState = EditTextState.IN_PROGRESS
        binding.emailFieldState = EditTextState.IN_PROGRESS

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.firstnameEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.firstnameFieldState = EditTextState.IN_PROGRESS
            } else {
                val isValid =
                    profileViewModel.isValidFirstName((v as EditText).text.toString())
                if (isValid) {
                    binding.firstnameFieldState = EditTextState.CORRECT
                } else {
                    binding.firstnameFieldState = EditTextState.INCORRECT
                }
            }
        }
        binding.lastnameEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.lastnameFieldState = EditTextState.IN_PROGRESS
            } else {
                val isValid =
                    profileViewModel.isValidLastName((v as EditText).text.toString())
                if (isValid) {
                    binding.lastnameFieldState = EditTextState.CORRECT
                } else {
                    binding.lastnameFieldState = EditTextState.INCORRECT
                }
            }
        }

        binding.emailEditText.addTextChangedListener {
            if (it != null && profileViewModel.isValidEmail(it.toString())) {
                binding.emailFieldState = EditTextState.CORRECT
            } else {
                binding.emailFieldState = EditTextState.IN_PROGRESS
            }
        }

        binding.confirmEditingButton.setOnClickListener {
            val firstName = binding.firstnameEditText.text.toString()
            val lastName = binding.lastnameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            if (!profileViewModel.isValidFirstName(firstName)) {
                binding.firstnameFieldState = EditTextState.INCORRECT
            }
            if (!profileViewModel.isValidLastName(lastName)) {
                binding.lastnameFieldState = EditTextState.INCORRECT
            }
            if (!profileViewModel.isValidEmail(email)) {
                binding.emailFieldState = EditTextState.INCORRECT
            }
            if (binding.firstnameFieldState?.isCorrect() == true
                && binding.lastnameFieldState?.isCorrect() == true
                && binding.emailFieldState?.isCorrect() == true
            ) {
                lifecycleScope.launch {
                    val result = profileViewModel.updateProfile(
                        UserProfile(
                            firstName,
                            lastName,
                            email,
                            changedUserProfile.phoneNumber,
                            changedUserProfile.jwtAccessToken,
                            changedUserProfile.jwtRefreshToken
                        )
                    )
                    if (result != null) {
                        findNavController().navigate(R.id.action_profileEditingFragment_to_profileFragment)
                    }
                    else {
                        TODO("Not yet implemented")
                    }
                }
            }
        }
    }
}