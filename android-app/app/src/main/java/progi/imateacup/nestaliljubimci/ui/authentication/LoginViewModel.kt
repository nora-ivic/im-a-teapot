package progi.imateacup.nestaliljubimci.ui.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import progi.imateacup.nestaliljubimci.model.networking.request.auth.LoginRequest
import progi.imateacup.nestaliljubimci.networking.ApiModule
import java.io.IOException


class LoginViewModel : ViewModel() {
    private val _loginResultLiveData = MutableLiveData<Boolean>()
    var loginResultLiveData : LiveData<Boolean> = _loginResultLiveData
    fun loginUser(
        username: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                postLoginRequest(
                    username,
                    password

                )
            } catch (err: Exception) {
                Log.e("Exception", err.toString())
                _loginResultLiveData.value = false
            }
        }
    }
    private suspend fun postLoginRequest(username: String, password: String) {
        val response = ApiModule.retrofit.login(request = LoginRequest(username = username, password = password))
        if (!response.isSuccessful) {
            throw IOException("Unsuccessful user login")
        }
        _loginResultLiveData.value = true
    }

}