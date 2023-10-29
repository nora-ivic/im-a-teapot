package progi.imateacup.nestaliljubimci.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    companion object {
        const val EMAIL_REGEX = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
        const val PASSWORD_REGEX = "^.{6,}$"
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    //private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.shelterNameField.visibility = View.GONE

        super.onViewCreated(view, savedInstanceState)
        //setOnRegistrationResultAction()
        initListeners()
    }

    /*private fun setOnRegistrationResultAction() {
        viewModel.registrationResultLiveData.observe(viewLifecycleOwner) { isRegistrationSuccessful ->
            if (isRegistrationSuccessful) {
                val direction = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(true)
                findNavController().navigate(direction)
            } else {
                Snackbar.make(binding.root, R.string.registration_unsuccessful, Snackbar.LENGTH_SHORT).show()
            }
        }
    }*/

    private fun initListeners() {
        with(binding) {

            emailField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updateEmailField()
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

            /* clicking the login button does not make the password field lose focus
            so this method should be present to update the password field if the user instead presses done
            passwordField.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updatePasswordField()
                }
                false
            }*/

            repeatPasswordField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updatePasswordsFields()
                }
            }

            /*repeatPasswordField.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updatePasswordsFields()
                }
                false
            }*/

            emailField.addTextChangedListener {
                registerButton.isEnabled = validateCredentials()
            }
            passwordField.addTextChangedListener {
                registerButton.isEnabled = validateCredentials()
            }
            repeatPasswordField.addTextChangedListener {
                registerButton.isEnabled = validateCredentials()
            }

            registerButton.setOnClickListener {

                //viewModel.registerUser(emailField.text.toString().trim(), passwordField.text.toString().trim())
            }

            registerAsShelterCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    shelterNameField.visibility = View.VISIBLE
                } else
                    shelterNameField.visibility = View.GONE
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        val emailRegex = Regex(EMAIL_REGEX)
        return email.matches(emailRegex)
    }

    private fun validatePassword(password: String): Boolean {/*Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character and must be at least 6 characters long*/
        val passwordRegex = Regex(PASSWORD_REGEX)
        return password.matches(passwordRegex)
    }

    private fun checkPasswordsMatch(): Boolean {
        return binding.passwordField.text.toString().trim() == binding.repeatPasswordField.text.toString().trim()
    }

    private fun validateCredentials(): Boolean {
        return validateEmail(binding.emailField.text.toString().trim()) && validatePassword(
            binding.passwordField.text.toString().trim()
        ) && checkPasswordsMatch()
    }

    private fun updateEmailField() {
        with(binding) {
            if (!validateEmail(emailField.text.toString().trim())) {

                emailFieldLayout.error = getString(R.string.email_error_message)
                emailFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)

            } else {
                emailFieldLayout.error = null
            }
        }
    }

    private fun updatePasswordField() {
        with(binding) {
            if (!validatePassword(passwordField.text.toString().trim())) {

                passwordFieldLayout.error = getString(R.string.password_error_message)
                passwordFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)

            } else {
                passwordFieldLayout.error = null
            }
        }
    }

    private fun updatePasswordsFields() {
        with(binding) {
            if (!checkPasswordsMatch()) {
                repeatPasswordFieldLayout.error = getString(R.string.passwords_dont_match)
                repeatPasswordFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)
                repeatPasswordField.setText("")
            } else {
                repeatPasswordFieldLayout.error = null
            }
        }
    }

}