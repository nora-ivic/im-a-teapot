package progi.imateacup.nestaliljubimci.ui.pets

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import progi.imateacup.nestaliljubimci.model.networking.response.ShelterResponse
import progi.imateacup.nestaliljubimci.networking.ApiModule
import java.io.IOException

class SheltersViewModel: ViewModel() {

    private val _sheltersLiveData = MutableLiveData<List<ShelterResponse>>()
    val sheltersLiveData: LiveData<List<ShelterResponse>> = _sheltersLiveData

    fun getShelters() {
        viewModelScope.launch {
            try {
                val shelters = sendGetSheltersRequest()
                _sheltersLiveData.value = shelters
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun sendGetSheltersRequest(): List<ShelterResponse> {
        val sheltersResponse =  ApiModule.retrofit.getShelters()

        if (sheltersResponse.isSuccessful) {
            return sheltersResponse.body()!!
        } else {
            throw IOException(sheltersResponse.errorBody()?.string() ?: "Unknown error")
        }
    }

}