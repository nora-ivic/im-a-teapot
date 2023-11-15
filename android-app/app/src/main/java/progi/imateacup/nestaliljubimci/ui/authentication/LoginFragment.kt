package progi.imateacup.nestaliljubimci.ui.authentication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.FragmentLoginBinding
import progi.imateacup.nestaliljubimci.networking.ApiModule

const val PREFERENCES_NAME = "Pets"

class LoginFragment : Fragment() {

    companion object {
        const val ACCESS_TOKEN = "ACCESS_TOKEN"
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        ApiModule.initRetrofit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAccessTokenObserver()
        setOnLoginResultAction()
        initListeners()
    }

    private fun setAccessTokenObserver() {
        viewModel.accessTokenLiveData.observe(viewLifecycleOwner) { accessToken ->
            sharedPreferences.edit {
                putString(ACCESS_TOKEN, accessToken)
            }
        }
    }

    private fun setOnLoginResultAction() {
        viewModel.loginResultLiveData.observe(viewLifecycleOwner) { isLoginSuccessful ->
            if (isLoginSuccessful) {
                val direction = LoginFragmentDirections.actionLoginFragmentToPetsFragment()
                findNavController().navigate(direction)
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.login_unsuccessful,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initListeners() {
        with(binding) {

            loginButton.isEnabled = validateCredentials()

            usernameField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updateUsernameField()
                }
            }
            passwordFieldLayout.setErrorIconOnClickListener {
                passwordFieldLayout.error = null
            }
            passwordField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updatePasswordField()
                }
            }
            passwordField.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                    updatePasswordField()
                }
                false
            }
            usernameField.addTextChangedListener {
                loginButton.isEnabled = validateCredentials()
            }
            passwordField.addTextChangedListener {
                loginButton.isEnabled = validateCredentials()
            }
            loginButton.setOnClickListener {
                viewModel.loginUser(
                    usernameField.text.toString().trim(),
                    passwordField.text.toString().trim(),
                )
            }
            guestButton.setOnClickListener {
                val direction = LoginFragmentDirections.actionLoginFragmentToPetsFragment()
                findNavController().navigate(direction)
            }
            registrationButton.setOnClickListener {
                val direction = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                findNavController().navigate(direction)
            }
        }
    }
    private fun updateUsernameField() {
        with(binding) {
            if (!validateUsername(usernameField.text.toString().trim())) {
                usernameFieldLayout.error =
                    getString(progi.imateacup.nestaliljubimci.R.string.username_error_message)
                usernameFieldLayout.setErrorTextAppearance(progi.imateacup.nestaliljubimci.R.style.ErrorTextAppearance)
            } else {
                usernameFieldLayout.error = null
            }
        }
    }

    private fun updatePasswordField() {
        with(binding) {
            if (!validatePassword(passwordField.text.toString().trim())) {
                passwordFieldLayout.error =
                    getString(R.string.password_error_message)
                passwordFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)
            } else {
                passwordFieldLayout.error = null
            }
        }
    }

    private fun validateUsername(username: String): Boolean {
        return username.length in 5..50
    }

    private fun validatePassword(password: String): Boolean {
        val passwordRegex = Regex(RegisterFragment.PASSWORD_REGEX)
        return password.matches(passwordRegex)
    }

    private fun validateCredentials(): Boolean {
        return validateUsername(binding.usernameField.text.toString().trim())
                && validatePassword(binding.passwordField.text.toString().trim())
    }

}