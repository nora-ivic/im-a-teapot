package progi.imateacup.nestaliljubimci.ui.advertDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import progi.imateacup.nestaliljubimci.model.networking.enums.AdvertisementCategory
import progi.imateacup.nestaliljubimci.model.networking.enums.PetSpecies
import progi.imateacup.nestaliljubimci.model.networking.request.auth.CreateAdvertRequest
import progi.imateacup.nestaliljubimci.networking.ApiModule
import java.io.IOException

class CreateAdvertViewModel : ViewModel() {

    private val _createAdvertResultLiveData = MutableLiveData<Boolean>()
    val createAdvertResultLiveData: LiveData<Boolean> = _createAdvertResultLiveData
    private val _accessTokenLiveData = MutableLiveData<String>()
    var accessTokenLiveData : LiveData<String> = _accessTokenLiveData
    fun postCreateAdvert(
        advert_category: AdvertisementCategory,
        pet_name: String,
        pet_species: PetSpecies,
        pet_color: String,
        pet_age: Int,
        date_time_lost: String,
        location_lost: String,
        description: String,
        is_in_shelter: Boolean
    ) {
        viewModelScope.launch {
            try {
                postCreateAdvertRequest(
                    advert_category,
                    pet_name,
                    pet_species,
                    pet_color,
                    pet_age,
                    date_time_lost,
                    location_lost,
                    description,
                    is_in_shelter
                )
            } catch (err: Exception) {
                Log.e("Exception", err.toString())
                _createAdvertResultLiveData.value = false
            }
        }
    }


    private suspend fun postCreateAdvertRequest(
        advert_category: AdvertisementCategory,
        pet_name: String,
        pet_species: PetSpecies,
        pet_color: String,
        pet_age: Int,
        date_time_lost: String,
        location_lost: String,
        description: String,
        is_in_shelter: Boolean
    ) {
        val response = ApiModule.retrofit.advert(request = CreateAdvertRequest(
            advert_category = advert_category,
            pet_name = pet_name,
            pet_species = pet_species,
            pet_color = pet_color,
            pet_age = pet_age,
            date_time_lost = date_time_lost,
            location_lost = location_lost,
            description = description,
            is_in_shelter = is_in_shelter
        )
        )
        if (!response.isSuccessful) {
            throw IOException("Unsuccessful user registration")
        }
        _createAdvertResultLiveData.value = true
    }
}
