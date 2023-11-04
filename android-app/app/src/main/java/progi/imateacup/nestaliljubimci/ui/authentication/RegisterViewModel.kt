package progi.imateacup.nestaliljubimci.ui.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.launch
import progi.imateacup.nestaliljubimci.model.networking.request.auth.RegisterRequest
import progi.imateacup.nestaliljubimci.networking.ApiModule

class RegisterViewModel : ViewModel() {

    private val _registrationResultLiveData = MutableLiveData<Boolean>()
    val registrationResultLiveData: LiveData<Boolean> = _registrationResultLiveData

    fun registerUser(
        username: String,
        password: String,
        email: String,
        phone: String,
        firstName: String,
        lastName: String,
        shelterName: String
    ) {
        viewModelScope.launch {
            try {
                postRegisterRequest(
                    username,
                    password,
                    email,
                    phone,
                    firstName,
                    lastName,
                    shelterName
                )
            } catch (err: Exception) {
                Log.e("Exception", err.toString())
                _registrationResultLiveData.value = false
            }
        }
    }

    private suspend fun postRegisterRequest(
        username: String,
        password: String,
        email: String,
        phone: String,
        firstName: String,
        lastName: String,
        shelterName: String
    ) {
        val response = ApiModule.retrofit.register(
            request = RegisterRequest(
                username = username,
                password = password,
                email = email,
                phone = phone,
                firstName = firstName,
                lastName = lastName,
                shelterName = shelterName,
                isShelter = shelterName.isNotEmpty()
            )
        )
        if (!response.isSuccessful) {
            throw IOException("Unsuccessful registration")
        }
        _registrationResultLiveData.value = true
    }
}