package progi.imateacup.nestaliljubimci.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

    companion object {
        const val EMAIL_REGEX = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
        const val PASSWORD_REGEX = "^.{6,}$"
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RegisterViewModel>()

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
        setOnRegistrationResultAction()
        initListeners()
    }

    private fun setOnRegistrationResultAction() {
        viewModel.registrationResultLiveData.observe(viewLifecycleOwner) { isRegistrationSuccessful ->
            if (isRegistrationSuccessful) {
                //val direction = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(true)
                //findNavController().navigate(direction)
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.registration_unsuccessful,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initListeners() {
        with(binding) {

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
            emailField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updateEmailField()
                }
            }
            phoneField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updatePhoneField()
                }
            }
            firstNameField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updateFirstNameField()
                }
            }
            lastNameField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updateLastNameField()
                }
            }
            shelterNameField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updateShelterNameField()
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

            usernameField.addTextChangedListener {
                registerButton.isEnabled = validateCredentials()
            }
            passwordField.addTextChangedListener {
                registerButton.isEnabled = validateCredentials()
            }
            repeatPasswordField.addTextChangedListener {
                registerButton.isEnabled = validateCredentials()
            }
            emailField.addTextChangedListener {
                registerButton.isEnabled = validateCredentials()
            }
            phoneField.addTextChangedListener {
                registerButton.isEnabled = validateCredentials()
            }
            firstNameField.addTextChangedListener {
                registerButton.isEnabled = validateCredentials()
            }
            lastNameField.addTextChangedListener {
                registerButton.isEnabled = validateCredentials()
            }
            shelterNameField.addTextChangedListener {
                registerButton.isEnabled = validateCredentials()
            }
            registerButton.setOnClickListener {
                viewModel.registerUser(
                    usernameField.text.toString().trim(),
                    passwordField.text.toString().trim(),
                    emailField.text.toString().trim(),
                    phoneField.text.toString().trim(),
                    firstNameField.text.toString().trim(),
                    lastNameField.text.toString().trim(),
                    shelterNameField.text.toString().trim()
                )
            }

            registerTypeButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    if (checkedId == registerAsPersonButton.id) {
                        shelterNameFieldLayout.visibility = View.GONE
                        shelterNameField.visibility = View.GONE
                        firstNameFieldLayout.visibility = View.VISIBLE
                        lastNameFieldLayout.visibility = View.VISIBLE
                    } else {
                        shelterNameFieldLayout.visibility = View.VISIBLE
                        shelterNameField.visibility = View.VISIBLE
                        firstNameFieldLayout.visibility = View.GONE
                        lastNameFieldLayout.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun validateUsername(username: String): Boolean {
        return username.length in 5..50
    }

    private fun validatePassword(password: String): Boolean {
        val passwordRegex = Regex(PASSWORD_REGEX)
        return password.matches(passwordRegex)
    }

    private fun checkPasswordsMatch(): Boolean {
        return binding.passwordField.text.toString()
            .trim() == binding.repeatPasswordField.text.toString().trim()
    }

    private fun validateEmail(email: String): Boolean {
        val emailRegex = Regex(EMAIL_REGEX)
        return email.matches(emailRegex)
    }

    private fun validatePhone(phone: String): Boolean {
        return phone.isNotEmpty()
    }

    private fun validateFirstAndLastName(name: String): Boolean {
        return name.length <= 50
    }

    private fun validateShelterName(shelterName: String): Boolean {
        return shelterName.length <= 100
    }

    private fun validateCredentials(): Boolean {
        return validateEmail(binding.emailField.text.toString().trim())
                && validatePassword(binding.passwordField.text.toString().trim())
                && checkPasswordsMatch()
                && validateShelterName(binding.shelterNameField.text.toString().trim())
                && validateFirstAndLastName(binding.firstNameField.text.toString().trim())
                && validateFirstAndLastName(binding.lastNameField.text.toString().trim())
                && validatePhone(binding.phoneField.text.toString().trim())
    }

    private fun updateUsernameField() {
        with(binding) {
            if (!validateUsername(usernameField.text.toString().trim())) {

                usernameFieldLayout.error = getString(R.string.username_error_message)
                usernameFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)

            } else {
                usernameFieldLayout.error = null
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
                repeatPasswordFieldLayout.error = getString(R.string.passwords_do_not_match)
                repeatPasswordFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)
                repeatPasswordField.setText("")
            } else {
                repeatPasswordFieldLayout.error = null
            }
        }
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

    private fun updatePhoneField() {
        with(binding) {
            if (!validatePhone(phoneField.text.toString().trim())) {
                phoneFieldLayout.error = getString(R.string.phone_error_message)
                phoneFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)
            } else {
                phoneFieldLayout.error = null
            }
        }
    }

    private fun updateFirstNameField() {
        with(binding) {
            if (!validateFirstAndLastName(firstNameField.text.toString().trim())) {
                firstNameFieldLayout.error = getString(R.string.first_name_error_message)
                firstNameFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)
            } else {
                firstNameFieldLayout.error = null
            }
        }
    }

    private fun updateLastNameField() {
        with(binding) {
            if (!validateFirstAndLastName(lastNameField.text.toString().trim())) {
                lastNameFieldLayout.error = getString(R.string.last_name_error_message)
                lastNameFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)
            } else {
                lastNameFieldLayout.error = null
            }
        }
    }

    private fun updateShelterNameField() {
        with(binding) {
            if (!validateShelterName(shelterNameField.text.toString().trim())) {
                shelterNameFieldLayout.error = getString(R.string.shelter_name_error_message)
                shelterNameFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)
            } else {
                shelterNameFieldLayout.error = null
            }
        }
    }
}
