package progi.imateacup.nestaliljubimci.ui.pets

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.launch
import progi.imateacup.nestaliljubimci.model.networking.response.Pet
import progi.imateacup.nestaliljubimci.networking.ApiModule

class PetsViewModel: ViewModel() {
    private var page = 0
    private var fetching = false
    
    private val _petsLiveData = MutableLiveData<List<Pet>?>()
    val petsLiveData: LiveData<List<Pet>?> = _petsLiveData

    private val _getPetsSuccessLiveData = MutableLiveData<Boolean>()
    val getPetsSuccessLiveData: LiveData<Boolean> = _getPetsSuccessLiveData
    
    fun getPets(networkAvailable: Boolean) {
        if (networkAvailable) {
            if (fetching) {
                return
            }
            fetching = true
            page++
            
            viewModelScope.launch { 
                try {
                    val newPosts = sendGetPetsRequest()
                    if (_petsLiveData.value == null) {
                        _petsLiveData.value = listOf<Pet>()
                    }
                    if (!newPosts.isNullOrEmpty()) {
                        val oldPosts = _petsLiveData.value
                        _petsLiveData.value = oldPosts!! + newPosts
                        _getPetsSuccessLiveData.value = true
                    }
                } catch (err: Exception) {
                    _getPetsSuccessLiveData.value = false
                    fetching = false
                    Log.d("DEBUG", err.message?:"no message")
                    Log.d( "DEBUG", err.stackTraceToString())
                }
            }
        }
        else {
            _getPetsSuccessLiveData.value = false
        }
    }

    private suspend fun sendGetPetsRequest(): List<Pet>? {
        val response = ApiModule.retrofit.getPets(page)

        if (!response.isSuccessful) {
            throw IOException("Failed to get missing pets adds")
        }
        else {
            return response.body()
        }
    }
}