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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentPhoneNumberConfirmationBinding
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.util.focusAndShowKeyboard
import ru.qwonix.android.foxwhiskers.util.onSend
import ru.qwonix.android.foxwhiskers.viewmodel.AuthenticationViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler
import ru.qwonix.android.foxwhiskers.viewmodel.TokenViewModel
import java.util.concurrent.TimeUnit


class PhoneNumberConfirmationFragment : Fragment(R.layout.fragment_phone_number_confirmation) {

    private val TAG = "PhoneNumberConfirmationFragment"

    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    private val args: PhoneNumberConfirmationFragmentArgs by navArgs()
    private lateinit var phoneNumber: String

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

        this.phoneNumber = args.phoneNumber

        countDownTimer.start()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pinCodeDigit1.focusAndShowKeyboard()

        authenticationViewModel.authenticatedResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "cant login to $phoneNumber – ${it.code} ${it.errorMessage}")
                    binding.hasError = true
                }

                is ApiResponse.Loading -> {

                }

                is ApiResponse.Success -> {
                    Log.i(TAG, "Success login $phoneNumber")
                    authenticationViewModel.authenticate(
                        it.data.jwtAccessToken,
                        it.data.jwtRefreshToken,
                        UserProfile(null, null, null, phoneNumber)
                    )
                    findNavController().navigate(R.id.action_phoneNumberConfirmationFragment_to_profileFragment)
                }
            }
        }

        binding.checkCodeButton.setOnClickListener {
            val pinCode =
                "${binding.pinCodeDigit1.text}${binding.pinCodeDigit2.text}${binding.pinCodeDigit3.text}${binding.pinCodeDigit4.text}"
            if (pinCode.length == 4 && pinCode.isDigitsOnly()) {
                authenticationViewModel.authenticate(
                    phoneNumber,
                    pinCode.toInt(),
                    object : CoroutinesErrorHandler {
                        override fun onError(message: String) {
                            Log.e(TAG, "check code error $phoneNumber – $message")
                        }
                    })
            } else {
                binding.hasError = true
            }
        }

        binding.sendAgainButton.setOnClickListener {
            binding.isTimerExpired = false
            authenticationViewModel.sendCode(phoneNumber, object : CoroutinesErrorHandler {
                override fun onError(message: String) {
                    Log.e(TAG, "send code error $phoneNumber – $message")

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