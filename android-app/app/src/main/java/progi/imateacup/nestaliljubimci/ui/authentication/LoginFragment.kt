package progi.imateacup.nestaliljubimci.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.FragmentLoginBinding
import progi.imateacup.nestaliljubimci.networking.ApiModule

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiModule.initRetrofit(requireContext())
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
        setOnLoginResultAction()
        initListeners()
    }

    private fun setOnLoginResultAction() {
        viewModel.loginResultLiveData.observe(viewLifecycleOwner) { isLoginSuccessful ->
            if (isLoginSuccessful) {
                //val direction = LoginFragmentDirections.
                //gore treba dodati pet fragment
                //findNavController().navigate(direction)
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
            registrationButton.isEnabled
            guestButton.isEnabled = isGuest()

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
                //val direction = LoginFragmentDirections.
                //gore treba dodati pet fragment
                //findNavController().navigate(direction)
            }
            registrationButton.setOnClickListener {
                val direction = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                findNavController().navigate(direction)
            }
        }
        //TODO
        // nakon sto se uspjesno napravi login i dobije se response sa tokenom
        // pozvati ApiModule.setSessionInfo(token), nakon toga ako sve radi
        // kak se spada o tokenu se vise nebi morali brinuti sljedeca 2 sata,
        // token istekne nakon 2 sata i kada se dobije response koji govori da
        // je istekao token preusmjeriti na login gdje ce se opet pozvati setSessionInfo
    }
    private fun isGuest(): Boolean{
        return true
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
                    getString(progi.imateacup.nestaliljubimci.R.string.password_error_message)
                passwordFieldLayout.setErrorTextAppearance(progi.imateacup.nestaliljubimci.R.style.ErrorTextAppearance)
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