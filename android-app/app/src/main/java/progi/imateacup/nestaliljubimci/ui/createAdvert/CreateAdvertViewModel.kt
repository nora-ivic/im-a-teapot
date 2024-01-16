package progi.imateacup.nestaliljubimci.ui.createAdvert

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import progi.imateacup.nestaliljubimci.model.networking.enums.AdvertisementCategory
import progi.imateacup.nestaliljubimci.model.networking.enums.PetSpecies
import progi.imateacup.nestaliljubimci.model.networking.request.auth.CreateAdvertRequest
import progi.imateacup.nestaliljubimci.networking.ApiModule
import java.io.File
import java.io.IOException

class CreateAdvertViewModel : ViewModel() {

    private val _advertAddedLiveData = MutableLiveData<Boolean>()
    val advertAddedLiveData: LiveData<Boolean> = _advertAddedLiveData

    private val _advertCoordinatesLiveData = MutableLiveData<String?>()
    val advertCoordinatesLiveData: LiveData<String?> = _advertCoordinatesLiveData

    private val _imageLinksLiveData = MutableLiveData<List<Uri>>()
    val imageLinksLiveData: LiveData<List<Uri>> = _imageLinksLiveData

    private val _imageUploadSuccessLiveData = MutableLiveData<Boolean>()
    val imageUploadSuccessLiveData: LiveData<Boolean> = _imageUploadSuccessLiveData

    fun advertAdvert(
        advert_category: AdvertisementCategory,
        pet_name: String?,
        pet_species: PetSpecies?,
        pet_color: String?,
        pet_age: Int?,
        date_time_lost: String?,
        location_lost: String?,
        description: String?,
        pictureLinks: List<String>
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
                    pictureLinks
                )
            } catch (err: Exception) {
                Log.e("Exception", err.toString())
                _advertAddedLiveData.value = false
            }
        }
    }

    fun uploadImage(picture: File) {
        viewModelScope.launch {
            try {
                val link = (ApiModule.BASE_URL.removeSuffix("/") + postImageRequest(picture))
                val currentList = _imageLinksLiveData.value ?: emptyList()
                val newList = currentList.toMutableList().apply { add(Uri.parse(link)) }
                _imageLinksLiveData.value = newList
                _imageUploadSuccessLiveData.value = true
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
                _imageUploadSuccessLiveData.value = false
            }

        }
    }

    private suspend fun postImageRequest(picture: File): String? {
        val response = ApiModule.retrofit.uploadImage(
            MultipartBody.Part.createFormData(
                "file",
                picture.name,
                picture.asRequestBody("image/*".toMediaType())
            )
        )
        if (!response.isSuccessful) {
            throw IOException("Failed to upload picture")
        }
        return response.body()?.link
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
        pictureLinks: List<String>
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
                pictureLinks = pictureLinks
            )
        )
        if (!response.isSuccessful) {
            throw IOException("Unsuccessful user registration")
        }
        return true
    }

    fun setAdvertCoordinates(coordinates: String?) {
        _advertCoordinatesLiveData.value = coordinates
    }
    fun setImageLink(links: List<Uri>) {
        _imageLinksLiveData.value = links
    }
}
