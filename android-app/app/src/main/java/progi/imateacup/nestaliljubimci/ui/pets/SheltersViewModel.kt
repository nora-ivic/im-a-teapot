package progi.imateacup.nestaliljubimci.ui.pets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import progi.imateacup.nestaliljubimci.model.networking.enums.DisplayState
import progi.imateacup.nestaliljubimci.model.networking.response.ShelterResponse
import progi.imateacup.nestaliljubimci.networking.ApiModule
import java.io.IOException

class SheltersViewModel: ViewModel() {

    private var page = 0
    private var fetching = false

    private val _sheltersLiveData = MutableLiveData<List<ShelterResponse>>()
    val sheltersLiveData: LiveData<List<ShelterResponse>> = _sheltersLiveData

    private val _DisplayStateLiveData = MutableLiveData<DisplayState>()
    val displayStateLiveData: LiveData<DisplayState> = _DisplayStateLiveData

    fun getShelters(networkAvailable: Boolean) {
        if (networkAvailable) {
            if (fetching) {
                return
            }
            _DisplayStateLiveData.value = DisplayState.LOADING
            page++
            fetching = true
            viewModelScope.launch {
                try {
                    val newShelters = sendGetSheltersRequest()
                    if (_sheltersLiveData.value == null) {
                        _sheltersLiveData.value = listOf<ShelterResponse>()
                    }
                    val oldShelters = _sheltersLiveData.value
                    if (!newShelters.isNullOrEmpty()) {
                        _sheltersLiveData.value = oldShelters!! + newShelters
                        _DisplayStateLiveData.value = DisplayState.SUCCESSGET
                    } else {
                        page--
                        if (oldShelters!!.isNotEmpty()) {
                            _DisplayStateLiveData.value = DisplayState.SUCCESSGET
                        } else {
                            _DisplayStateLiveData.value = DisplayState.NOPOSTS
                        }
                    }
                    fetching = false
                } catch (e: IOException) {
                    _DisplayStateLiveData.value = DisplayState.ERRORGET
                    fetching = false
                }
            }
        } else {
            _DisplayStateLiveData.value = DisplayState.ERRORGET
            fetching = false
        }
    }

    private suspend fun sendGetSheltersRequest(): List<ShelterResponse> {
        val sheltersResponse =  ApiModule.retrofit.getShelters(page)

        if (sheltersResponse.isSuccessful) {
            return sheltersResponse.body()!!
        } else {
            throw IOException(sheltersResponse.errorBody()?.string() ?: "Unknown error")
        }
    }

}