package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentProfileEditingBinding
import ru.qwonix.android.foxwhiskers.entity.Client
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.util.EditTextState
import ru.qwonix.android.foxwhiskers.util.Utils
import ru.qwonix.android.foxwhiskers.util.onImeAction
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler
import ru.qwonix.android.foxwhiskers.viewmodel.ProfileViewModel

@AndroidEntryPoint
class ProfileEditingFragment : Fragment(R.layout.fragment_profile_editing) {

    private val TAG = "ProfileEditingFragment"

    private val args: ProfileEditingFragmentArgs by navArgs()
    private lateinit var client: Client

    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var binding: FragmentProfileEditingBinding

    companion object {

        @JvmStatic
        @BindingAdapter("state")
        fun setBackgroundStyle(view: View, drawableId: Int) {
            view.setBackgroundResource(drawableId)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEditingBinding.inflate(inflater, container, false)

        binding.firstnameFieldState = EditTextState.IN_PROGRESS
        binding.lastnameFieldState = EditTextState.IN_PROGRESS
        binding.emailFieldState = EditTextState.IN_PROGRESS

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.client = args.client

        binding.client = this.client

        profileViewModel.clientUpdateResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")

                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful client updating")
                    findNavController().navigate(R.id.action_profileEditingFragment_to_profileFragment)
                }
            }
        }

        binding.confirmEditingButton.setOnClickListener {
            val phoneNumber = client.phoneNumber
            val firstName = binding.firstnameEditText.text.toString()
            val lastName = binding.lastnameEditText.text.toString()
            val email = binding.emailEditText.text.toString()

            var canUpdate = true

            if (Utils.isValidFirstName(firstName)) {
                binding.firstnameFieldState = EditTextState.CORRECT
            } else {
                binding.firstnameFieldState = EditTextState.INCORRECT
                canUpdate = false
            }
            if (Utils.isValidLastName(lastName)) {
                binding.lastnameFieldState = EditTextState.CORRECT
            } else {
                binding.lastnameFieldState = EditTextState.INCORRECT
                canUpdate = false
            }
            if (Utils.isValidEmail(email)) {
                binding.emailFieldState = EditTextState.CORRECT
            } else {
                binding.emailFieldState = EditTextState.INCORRECT
                canUpdate = false
            }
            if (canUpdate) {
                profileViewModel.update(
                    phoneNumber,
                    firstName,
                    lastName,
                    email,
                    object : CoroutinesErrorHandler {
                        override fun onError(message: String) {
                            TODO("Not yet implemented")
                        }
                    })
            }
        }

        binding.firstnameEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.firstnameFieldState = EditTextState.IN_PROGRESS
            } else {
                val editText = v as EditText
                editText.setText(editText.text.trim(), TextView.BufferType.EDITABLE)

                val isValid =
                    Utils.isValidFirstName(editText.text.toString())
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
                val editText = v as EditText
                editText.setText(editText.text.trim(), TextView.BufferType.EDITABLE)

                val isValid =
                    Utils.isValidLastName(editText.text.toString())
                if (isValid) {
                    binding.lastnameFieldState = EditTextState.CORRECT
                } else {
                    binding.lastnameFieldState = EditTextState.INCORRECT
                }
            }
        }

        binding.emailEditText.addTextChangedListener {
            if (it != null && Utils.isValidEmail(it.toString())) {
                binding.emailFieldState = EditTextState.CORRECT
            } else {
                binding.emailFieldState = EditTextState.IN_PROGRESS
            }
        }

        binding.emailEditText.onImeAction(EditorInfo.IME_ACTION_DONE) {
            if (Utils.isValidEmail(binding.emailEditText.text.toString())) {
                binding.emailFieldState = EditTextState.CORRECT
            } else {
                binding.emailFieldState = EditTextState.INCORRECT
            }
        }
    }
}
