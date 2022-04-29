package com.camelcc.overcooked

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginFragment : Fragment() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loading: CircularProgressIndicator
    private lateinit var login: Button

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        username = view.findViewById(R.id.username)
        password = view.findViewById(R.id.pwd)
        loading = view.findViewById(R.id.loading)
        login = view.findViewById(R.id.login)

        login.setOnClickListener {
            viewModel.login(username.text.toString(), password.text.toString())
        }

        viewModel.loginState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                login.isEnabled = state != LoginState.Logining
                loading.visibility = if (state == LoginState.Logining) View.VISIBLE else View.GONE
                if (state == LoginState.LoginSuccessful) {
                    findNavController().popBackStack()
                }
            }
            .launchIn(lifecycleScope)
    }
}