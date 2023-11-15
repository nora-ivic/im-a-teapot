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
import progi.imateacup.nestaliljubimci.model.networking.response.LoginResponse
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val _loginResultLiveData = MutableLiveData<Boolean>()
    var loginResultLiveData : LiveData<Boolean> = _loginResultLiveData

    private val _accessTokenLiveData = MutableLiveData<String>()
    var accessTokenLiveData : LiveData<String> = _accessTokenLiveData

    fun loginUser( username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = postLoginRequest( username, password)
                _accessTokenLiveData.value = response.body()?.token
                if (response.body()?.token != null) {
                    ApiModule.setSessionInfo(response.body()?.token!!)
                }
            } catch (err: Exception) {
                Log.e("Exception", err.toString())
                _loginResultLiveData.value = false
            }
        }
    }
    private suspend fun postLoginRequest(username: String, password: String): Response<LoginResponse> {
        val response = ApiModule.retrofit.login(request = LoginRequest(username = username, password = password))
        if (!response.isSuccessful) {
            throw IOException("Unsuccessful user login")
        }
        _loginResultLiveData.value = true
        return response
    }

}