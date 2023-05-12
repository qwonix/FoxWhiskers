package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentPhoneNumberConfirmationBinding
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.util.focusAndShowKeyboard
import ru.qwonix.android.foxwhiskers.util.onSend
import ru.qwonix.android.foxwhiskers.viewmodel.AuthenticationViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PhoneNumberConfirmationFragment : Fragment(R.layout.fragment_phone_number_confirmation) {

    private val TAG = "PhoneConfirmFragment"

    private val authenticationViewModel: AuthenticationViewModel by viewModels()
    private val args: PhoneNumberConfirmationFragmentArgs by navArgs()

    private lateinit var binding: FragmentPhoneNumberConfirmationBinding

    private var countDownTimer: CountDownTimer =
        object : CountDownTimer(((3 * 2 * 1000).toLong()), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds =
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        minutes
                    )
                binding.countdown.text = "$minutes : $seconds"
            }

            override fun onFinish() {
                binding.isTimerExpired = true
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneNumberConfirmationBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            isTimerExpired = false
            hasError = false
        }
        countDownTimer.start()

        Log.i(TAG, "onCreateView")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pinCodeDigit1.focusAndShowKeyboard()

        authenticationViewModel.authenticationResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                    binding.hasError = true
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")


                is ApiResponse.Success -> {
                    Log.i(TAG, "Success login")
                    findNavController().navigate(R.id.action_phoneNumberConfirmationFragment_to_profileFragment)
                }
            }
        }

        binding.checkCodeButton.setOnClickListener {
            val pinCode =
                "${binding.pinCodeDigit1.text}${binding.pinCodeDigit2.text}${binding.pinCodeDigit3.text}${binding.pinCodeDigit4.text}"
            if (pinCode.length == 4 && pinCode.isDigitsOnly()) {
                authenticationViewModel.authenticate(
                    args.phoneNumber,
                    pinCode.toInt(),
                    object : CoroutinesErrorHandler {
                        override fun onError(message: String) {
                            TODO("Not yet implemented")
                        }
                    })
            } else {
                binding.hasError = true
            }
        }

        binding.sendAgainButton.setOnClickListener {
            binding.isTimerExpired = false
            authenticationViewModel.sendCodeAgain(
                args.phoneNumber,
                object : CoroutinesErrorHandler {
                    override fun onError(message: String) {
                        TODO("Not yet implemented")
                    }
                })
            countDownTimer.start()
        }


        binding.pinCodeDigit1.addTextChangedListener {
            binding.hasError = false

            if (it != null) {
                if (it.length == 1 && it.isDigitsOnly()) {
                    binding.pinCodeDigit2.requestFocus()
                }
            }
        }

        binding.pinCodeDigit2.addTextChangedListener {
            binding.hasError = false

            if (it != null) {
                if (it.length == 1 && it.isDigitsOnly()) {
                    binding.pinCodeDigit3.requestFocus()
                }

                if (it.isEmpty()) {
                    binding.pinCodeDigit1.requestFocus()
                }
            }
        }

        binding.pinCodeDigit3.addTextChangedListener {
            binding.hasError = false

            if (it != null) {
                if (it.length == 1 && it.isDigitsOnly()) {
                    binding.pinCodeDigit4.requestFocus()
                }
                if (it.isEmpty()) {
                    binding.pinCodeDigit2.requestFocus()
                }
            }
        }

        binding.pinCodeDigit4.addTextChangedListener {
            binding.hasError = false

            if (it != null) {
                if (it.isEmpty()) {
                    binding.pinCodeDigit3.requestFocus()
                } else {
                    binding.checkCodeButton.callOnClick()
                }
            }
        }

        binding.pinCodeDigit4.onSend {
            binding.checkCodeButton.callOnClick()
        }
    }
}