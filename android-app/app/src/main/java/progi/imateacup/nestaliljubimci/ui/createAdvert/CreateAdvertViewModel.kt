package progi.imateacup.nestaliljubimci.ui.createAdvert

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

    private val _advertAddedLiveData = MutableLiveData<Boolean>()
    val advertAddedLiveData: LiveData<Boolean> = _advertAddedLiveData

    fun advertAdvert(
        advert_category: AdvertisementCategory,
        pet_name: String?,
        pet_species: PetSpecies?,
        pet_color: String?,
        pet_age: Int?,
        date_time_lost: String?,
        location_lost: String?,
        description: String?,
    ) {
        viewModelScope.launch {
            try {
                _advertAddedLiveData.value = postAdvert(
                    advert_category,
                    pet_name,
                    pet_species,
                    pet_color,
                    pet_age,
                    date_time_lost,
                    location_lost,
                    description,
                )
            } catch (err: Exception) {
                Log.e("Exception", err.toString())
                _advertAddedLiveData.value = false
            }
        }
    }

    private suspend fun postAdvert(
        advert_category: AdvertisementCategory,
        pet_name: String?,
        pet_species: PetSpecies?,
        pet_color: String?,
        pet_age: Int?,
        date_time_lost: String?,
        location_lost: String?,
        description: String?,
    ): Boolean {
        val response = ApiModule.retrofit.addAdvert(
            request = CreateAdvertRequest(
                advert_category = advert_category,
                pet_name = pet_name,
                pet_species = pet_species,
                pet_color = pet_color,
                pet_age = pet_age,
                date_time_lost = date_time_lost,
                location_lost = location_lost,
                description = description,
            )
        )
        if (!response.isSuccessful) {
            throw IOException("Unsuccessful user registration")
        }
        //TODO: refetch
        return true
    }
}
