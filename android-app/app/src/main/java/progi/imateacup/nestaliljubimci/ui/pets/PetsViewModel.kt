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
import progi.imateacup.nestaliljubimci.model.networking.enums.PetsDisplayState
import progi.imateacup.nestaliljubimci.model.networking.response.Pet
import progi.imateacup.nestaliljubimci.networking.ApiModule
import retrofit2.Response

class PetsViewModel : ViewModel() {
    private var page = 0
    private var fetching = false
    private var lastFilter: SearchFilter? = null
    private var lastGetMyPets = false

    val filterPresentLiveData = MutableLiveData<Boolean>()

    private val _petsLiveData = MutableLiveData<List<Pet>?>()
    val petsLiveData: LiveData<List<Pet>?> = _petsLiveData

    private val _petsDisplayStateLiveData = MutableLiveData<PetsDisplayState>()
    val petsDisplayStateLiveData: LiveData<PetsDisplayState> = _petsDisplayStateLiveData

    fun getPets(networkAvailable: Boolean, filter: SearchFilter, getMyPets: Boolean, reset: Boolean) {
        Log.d("PetsViewModel", "Getting pets")
        if (networkAvailable) {
            if (fetching) {
                Log.d("PetsViewModel", "Already fetching")
                return
            }

            if (lastFilter != filter || lastGetMyPets != getMyPets || reset) {
                Log.d("PetsViewModel", "Reset")
                lastFilter = filter
                lastGetMyPets = getMyPets
                page = 0
                _petsLiveData.value = null
            }
            _petsDisplayStateLiveData.value = PetsDisplayState.LOADING
            fetching = true
            page++

            viewModelScope.launch {
                try {
                    val newPosts = if (getMyPets) {
                        sendGetMyPetsRequest()
                    } else {
                        sendGetPetsRequest(filter)
                    }
                    if (_petsLiveData.value == null) {
                        _petsLiveData.value = listOf<Pet>()
                    }
                    val oldPosts = _petsLiveData.value
                    if (!newPosts.isNullOrEmpty()) {
                        _petsLiveData.value = oldPosts!! + newPosts
                        _petsDisplayStateLiveData.value = PetsDisplayState.SUCCESSGET
                    } else {
                        page--
                        if (oldPosts!!.isNotEmpty()) {
                            _petsDisplayStateLiveData.value = PetsDisplayState.SUCCESSGET
                        } else {
                            _petsDisplayStateLiveData.value = PetsDisplayState.NOPOSTS
                        }
                    }
                    fetching = false
                } catch (err: Exception) {
                    Log.d("PetsViewModel", "Failed to get pets: ${err.message}")
                    Log.d("PetsViewModel", err.stackTraceToString())
                    _petsDisplayStateLiveData.value = PetsDisplayState.ERRORGET
                    fetching = false
                }
            }
        } else {
            fetching = false
            _petsDisplayStateLiveData.value = PetsDisplayState.ERRORGET
        }
    }

    private suspend fun sendGetMyPetsRequest(): List<Pet>? {
        val response = ApiModule.retrofit.getMyPets(page = page)

        if (!response.isSuccessful) {
            throw IOException("Failed to get my missing pets adds")
        } else {
            return response.body()
        }
    }

    private suspend fun sendGetPetsRequest(filter: SearchFilter): List<Pet>? {
        val response = ApiModule.retrofit.getPets(page = page, filter = filter.toQueryMap())

        if (!response.isSuccessful) {
            throw IOException("Failed to get missing pets adds")
        } else {
            return response.body()
        }
    }

    fun deleteAdvert(advertId: Int, networkAvailable: Boolean) {
        if (networkAvailable) {
            viewModelScope.launch {
                try {
                    sendDeleteRequest(advertId)
                    _petsDisplayStateLiveData.value = PetsDisplayState.SUCCESSDELETE
                } catch (err: Exception) {
                    _petsDisplayStateLiveData.value = PetsDisplayState.ERRORDELETE
                }
            }
        } else {
            _petsDisplayStateLiveData.value = PetsDisplayState.ERRORDELETE
        }
    }

    private suspend fun sendDeleteRequest(advertId: Int): Response<Unit> {
        val response = ApiModule.retrofit.deleteAdvert(advertId)

        if (!response.isSuccessful) {
            throw IOException("Failed to delete advert")
        }
        return response
    }
}