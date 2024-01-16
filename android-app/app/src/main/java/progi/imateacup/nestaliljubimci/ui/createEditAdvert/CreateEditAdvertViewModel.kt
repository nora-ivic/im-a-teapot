package progi.imateacup.nestaliljubimci.ui.createEditAdvert

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
import progi.imateacup.nestaliljubimci.model.networking.request.auth.CreateEditAdvertRequest
import progi.imateacup.nestaliljubimci.model.networking.response.Advert
import progi.imateacup.nestaliljubimci.model.networking.response.CreateEditAdvertResponse
import progi.imateacup.nestaliljubimci.networking.ApiModule
import java.io.File
import java.io.IOException

class CreateEditAdvertViewModel : ViewModel() {

    private val _advertIdLiveData = MutableLiveData<Int?>()
    val advertIdLiveData: LiveData<Int?> = _advertIdLiveData

    private val _advertCoordinatesLiveData = MutableLiveData<String?>()
    val advertCoordinatesLiveData: LiveData<String?> = _advertCoordinatesLiveData

    private val _imageLinksLiveData = MutableLiveData<List<Uri>>()
    val imageLinksLiveData: LiveData<List<Uri>> = _imageLinksLiveData

    private val _imageUploadSuccessLiveData = MutableLiveData<Boolean>()
    val imageUploadSuccessLiveData: LiveData<Boolean> = _imageUploadSuccessLiveData

    private val _advertLiveData = MutableLiveData<Advert?>()
    val advertLiveData: LiveData<Advert?> = _advertLiveData

    private val _advertFetchSuccessLiveData = MutableLiveData<Boolean>()
    val advertFetchSuccessLiveData: LiveData<Boolean> = _advertFetchSuccessLiveData

    private val _accessTokenExpiredLiveData = MutableLiveData<Boolean>()
    val accessTokenExpiredLiveData: LiveData<Boolean> = _accessTokenExpiredLiveData



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
                _advertIdLiveData.value = postAdvert(
                    advert_category,
                    pet_name,
                    pet_species,
                    pet_color,
                    pet_age,
                    date_time_lost,
                    location_lost,
                    description,
                    pictureLinks
                )?.advertId
            } catch (err: Exception) {
                if (err.localizedMessage == "Unauthorized") {
                    _accessTokenExpiredLiveData.value = true
                }
                _advertIdLiveData.value = null
            }
        }
    }


    fun editAdvert(
        advert_id: Int,
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
                _advertIdLiveData.value = putAdvert(
                    advert_id,
                    advert_category,
                    pet_name,
                    pet_species,
                    pet_color,
                    pet_age,
                    date_time_lost,
                    location_lost,
                    description,
                    pictureLinks
                )?.advertId
            } catch (err: Exception) {

                if (err.localizedMessage == "Unauthorized") {
                    _accessTokenExpiredLiveData.value = true
                }
                _advertIdLiveData.value = null
            }
        }
    }

    fun getAdvertDetails(advertId: Int) {
        viewModelScope.launch {
            try {
                val advert = fetchAdvertDetails(advertId)
                if (advert?.petName == "?")
                    advert.petName = null
                _advertLiveData.value = advert
                _advertFetchSuccessLiveData.value = true
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
                _advertFetchSuccessLiveData.value = false
            }
        }
    }

    private suspend fun fetchAdvertDetails(advertId: Int): Advert? {
        val response = ApiModule.retrofit.getAdvertDetails(advertId = advertId)

        if (response.code() == 401) {
            throw IOException("Unauthorized")
        }

        if (!response.isSuccessful)
            throw IOException("Failed to get advert details")
        else
            return response.body()
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
        if (response.code() == 401) {
            throw IOException("Unauthorized")
        }

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
    ): CreateEditAdvertResponse? {
        val response = ApiModule.retrofit.addAdvert(
            request = CreateEditAdvertRequest(
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
        if (response.code() == 401) {
            throw IOException("Unauthorized")
        }
        if (!response.isSuccessful) {
            throw IOException("Neuspješno dodavanje oglasa.")
        }
        return response.body()
    }


    private suspend fun putAdvert(
        advert_id: Int,
        advert_category: AdvertisementCategory,
        pet_name: String?,
        pet_species: PetSpecies?,
        pet_color: String?,
        pet_age: Int?,
        date_time_lost: String?,
        location_lost: String?,
        description: String?,
        pictureLinks: List<String>
    ): CreateEditAdvertResponse? {
        val response = ApiModule.retrofit.putAdvert(
            advertId = advert_id,
            request = CreateEditAdvertRequest(
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
        if (response.code() == 401) {
            throw IOException("Unauthorized")
        }
        if (!response.isSuccessful) {
            throw IOException("Neuspješno uređivanje oglasa.")
        }

        return response.body()
    }

    fun setAdvertCoordinates(coordinates: String?) {
        _advertCoordinatesLiveData.value = coordinates
    }

    fun setImageLinks(links: List<Uri>) {
        _imageLinksLiveData.value = links
    }
}
