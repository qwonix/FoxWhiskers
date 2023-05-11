package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentPhoneNumberInputBinding
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.util.focusAndShowKeyboard
import ru.qwonix.android.foxwhiskers.viewmodel.AuthenticationViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler
import ru.tinkoff.decoro.FormattedTextChangeListener
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher


class PhoneNumberInputFragment : Fragment(R.layout.fragment_phone_number_input) {

    private val TAG = "PhoneInputFragment"

    private lateinit var binding: FragmentPhoneNumberInputBinding
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneNumberInputBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            hasError = false
            isMaskFilled = false
        }
        Log.i(TAG, "onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.phoneNumberTextView.focusAndShowKeyboard()

        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        val watcher: FormatWatcher = MaskFormatWatcher(mask)
        watcher.installOn(binding.phoneNumberTextView)

        watcher.setCallback(object : FormattedTextChangeListener {
            override fun beforeFormatting(oldValue: String?, newValue: String?): Boolean {
                return false
            }

            override fun onTextFormatted(formatter: FormatWatcher, newFormattedText: String?) {
                val isMaskFilled = formatter.mask.filled()
                val hasError = binding.hasError ?: false

                binding.hasError = when {
                    hasError == true && isMaskFilled == false -> false
                    hasError == false && isMaskFilled == false -> false
                    hasError == true && isMaskFilled == true -> false
                    hasError == false && isMaskFilled == false -> true
                    else -> false
                }
                binding.isMaskFilled = isMaskFilled
            }
        })

        authenticationViewModel.sendCodeResponse.observe(viewLifecycleOwner) {
            Log.i(TAG, "observe ${it}")
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                    binding.hasError = true
                }

                is ApiResponse.Loading -> {
                    Log.i(TAG, "loading")
                }

                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful code sending")
                    findNavController().navigate(R.id.action_phoneNumberInputFragment_to_phoneNumberConfirmationFragment)
                }
            }
        }

        binding.sendCodeButton.setOnClickListener {
            if (binding.hasError == false && binding.isMaskFilled == true) {
                val phoneNumber = binding.phoneNumberTextView.text.toString()

                authenticationViewModel.sendCode(phoneNumber, object : CoroutinesErrorHandler {
                    override fun onError(message: String) {
                        TODO("Not yet implemented")
                    }
                })

            } else {
                binding.hasError = true
            }
        }
    }
}