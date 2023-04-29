package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentPhoneNumberConfirmationBinding
import java.util.concurrent.TimeUnit


class PhoneNumberConfirmationFragment : Fragment(R.layout.fragment_phone_number_confirmation) {

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkCodeButton.setOnClickListener {
            binding.hasError = false
        }

        binding.sendAgainButton.setOnClickListener {
            binding.isTimerExpired = false
            countDownTimer.start()
        }

        binding.pinCodeDigit1.focusAndShowKeyboard()

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
    }

}