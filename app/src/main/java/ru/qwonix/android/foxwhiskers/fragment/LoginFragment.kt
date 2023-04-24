package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentLoginBinding
import ru.qwonix.android.foxwhiskers.viewmodel.MenuViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {
    companion object {
        fun newInstance() = LoginFragment()
    }
    private lateinit var binding: FragmentLoginBinding
    private val menuViewModel: MenuViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}