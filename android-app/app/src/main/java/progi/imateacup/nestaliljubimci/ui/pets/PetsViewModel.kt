package progi.imateacup.nestaliljubimci.ui.pets

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.launch
import progi.imateacup.nestaliljubimci.model.networking.entities.SearchFilter
import progi.imateacup.nestaliljubimci.model.networking.entities.toQueryMap
import progi.imateacup.nestaliljubimci.model.networking.enums.AppState
import progi.imateacup.nestaliljubimci.model.networking.response.Pet
import progi.imateacup.nestaliljubimci.networking.ApiModule

class PetsViewModel : ViewModel() {
    private var page = 0
    private var fetching = false
    private var lastFilter: SearchFilter? = null

    private val _petsLiveData = MutableLiveData<List<Pet>?>()
    val petsLiveData: LiveData<List<Pet>?> = _petsLiveData

    private val _appStateLiveData = MutableLiveData<AppState>()
    val appStateLiveData: LiveData<AppState> = _appStateLiveData

    fun getPets(networkAvailable: Boolean, filter: SearchFilter) {
        Log.d("PetsViewModel", "Getting pets")
        if (networkAvailable) {
            if (fetching) {
                Log.d("PetsViewModel", "Already fetching")
                return
            }
            if (lastFilter != filter) {
                Log.d("PetsViewModel", "Filter changed")
                lastFilter = filter
                page = 0
                _petsLiveData.value = null
            }
            _appStateLiveData.value = AppState.LOADING
            fetching = true
            page++

            viewModelScope.launch {
                try {
                    Log.d("PetsViewModel", "In try block")
                    val newPosts = sendGetPetsRequest(filter)
                    if (_petsLiveData.value == null) {
                        _petsLiveData.value = listOf<Pet>()
                    }
                    val oldPosts = _petsLiveData.value
                    if (!newPosts.isNullOrEmpty()) {
                        _petsLiveData.value = oldPosts!! + newPosts
                        _appStateLiveData.value = AppState.SUCCESS

                    } else {
                        if (oldPosts!!.isNotEmpty()) {
                            _appStateLiveData.value = AppState.SUCCESS
                        } else {
                            _appStateLiveData.value = AppState.ERROR
                        }
                    }
                    fetching = false
                } catch (err: Exception) {
                    Log.d("PetsViewModel", "Failed to get pets: ${err.message}")
                    Log.d("PetsViewModel", err.stackTraceToString())
                    _appStateLiveData.value = AppState.ERROR
                    fetching = false
                }
            }
        } else {
            Log.d("PetsViewModel", "No internet connection")
            fetching = false
            _appStateLiveData.value = AppState.ERROR
        }
    }

    private suspend fun sendGetPetsRequest(filter: SearchFilter): List<Pet>? {
        Log.d("PetsViewModel", "Sending request for page $page")
        val response = ApiModule.retrofit.getPets(page = page, filter = filter.toQueryMap())

        if (!response.isSuccessful) {
            throw IOException("Failed to get missing pets adds")
        } else {
            return response.body()
        }
    }
}