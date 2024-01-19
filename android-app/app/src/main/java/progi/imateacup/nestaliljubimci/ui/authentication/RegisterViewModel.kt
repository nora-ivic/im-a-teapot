package progi.imateacup.nestaliljubimci.ui.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import progi.imateacup.nestaliljubimci.model.networking.request.auth.RegisterRequest
import progi.imateacup.nestaliljubimci.networking.ApiModule
import java.io.IOException

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
        shelterName: String,
        isShelter: Boolean
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
                    shelterName,
                    isShelter
                )
            } catch (err: Exception) {
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
        shelterName: String,
        isShelter: Boolean
    ) {
        if (isShelter) {
            val response = ApiModule.retrofit.register(
                request = RegisterRequest(
                    username = username,
                    password = password,
                    email = email,
                    phone = phone,
                    firstName = null,
                    lastName = null,
                    shelterName = shelterName,
                    isShelter = true
                )
            )
            if (!response.isSuccessful) {
                throw IOException("Unsuccessful shelter registration")
            }
        } else {
            val response = ApiModule.retrofit.register(
                request = RegisterRequest(
                    username = username,
                    password = password,
                    email = email,
                    phone = phone,
                    firstName = firstName,
                    lastName = lastName,
                    shelterName = null,
                    isShelter = false
                )
            )
            if (!response.isSuccessful) {
                throw IOException("Unsuccessful user registration")
            }
        }
        _registrationResultLiveData.value = true
    }
}