package progi.imateacup.nestaliljubimci.ui.detailed_view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import progi.imateacup.nestaliljubimci.model.networking.request.auth.AddCommentRequest
import progi.imateacup.nestaliljubimci.model.networking.entities.Advert
import progi.imateacup.nestaliljubimci.model.networking.entities.Comment
import progi.imateacup.nestaliljubimci.model.networking.entities.Pet
import progi.imateacup.nestaliljubimci.model.networking.entities.Shelter
import progi.imateacup.nestaliljubimci.networking.ApiModule
import java.io.File
import java.io.IOException

class DetailedViewModel : ViewModel() {

    private val _commentsLiveData = MutableLiveData<List<Comment>>()
    val commentsLiveData: LiveData<List<Comment>> = _commentsLiveData

    private val _advertLiveData = MutableLiveData<Advert>()
    val advertLiveData: LiveData<Advert> = _advertLiveData

    private val _petLiveData = MutableLiveData<Pet>()
    val petLiveData: LiveData<Pet> = _petLiveData

    private val _shelterLiveData = MutableLiveData<Shelter>()
    val shelterLiveData: LiveData<Shelter> = _shelterLiveData

    private val _commentAddedLiveData = MutableLiveData<Boolean>()
    val commentAddedLiveData: LiveData<Boolean> = _commentAddedLiveData

    private val _advertFetchSuccessLiveData = MutableLiveData<Boolean>()
    val advertFetchSuccessLiveData: LiveData<Boolean> = _advertFetchSuccessLiveData

    private val _petFetchSuccessLiveData = MutableLiveData<Boolean>()
    val petFetchSuccessLiveData: LiveData<Boolean> = _petFetchSuccessLiveData

    private val _shelterFetchSuccessLiveData = MutableLiveData<Boolean>()
    val shelterFetchSuccessLiveData: LiveData<Boolean> = _shelterFetchSuccessLiveData

    private var imageDir: File? = null
    fun getAdvertInfo(advertId: String) {
        viewModelScope.launch {
            try {
                _advertLiveData.value = fetchAdvertInfo(advertId)
                _advertFetchSuccessLiveData.value = true
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
                _advertFetchSuccessLiveData.value = false
            }
        }
    }

    private suspend fun fetchAdvertInfo(advertId: String): Advert =
        ApiModule.retrofit.getDetailedAdvert(advertId = advertId).advert

    fun getShelter(shelterId: String) {
        viewModelScope.launch {
            try {
                _shelterLiveData.value = fetchShelter(shelterId)
                _shelterFetchSuccessLiveData.value = true
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
                _shelterFetchSuccessLiveData.value = false
            }
        }
    }

    private suspend fun fetchShelter(shelterId: String): Shelter =
        ApiModule.retrofit.getShelter(shelterId = shelterId).shelter

    fun getPet(petId: String) {
        viewModelScope.launch {
            try {
                _petLiveData.value = fetchPet(petId)
                _petFetchSuccessLiveData.value = true
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
                _petFetchSuccessLiveData.value = false
            }
        }
    }

    private suspend fun fetchPet(petId: String): Pet =
        ApiModule.retrofit.getPet(petId = petId).pet
    fun getComments(advertId: String) {
        viewModelScope.launch {
            try {
                val comments = fetchComments(advertId)
                _commentsLiveData.value = comments
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }
        }
    }

    private suspend fun fetchComments(advertId: String): List<Comment> {
        val result = ApiModule.retrofit.getComments(advertId = advertId)
        if (!result.isSuccessful) {
            throw IOException("Unable to get comments")
        }
        return result.body()!!.comments
    }

    fun advertComment(
        userId: String,
        advertId: String,
        text: String,
        pictureId: String,
        location: String
    ) {
        viewModelScope.launch {
            try {
                _commentAddedLiveData.value =
                    postComment(userId, advertId, text, pictureId, location)

            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
                _commentAddedLiveData.value = false
            }
        }
    }

    private suspend fun postComment(
        userId: String,
        advertId: String,
        text: String,
        pictureId: String,
        location: String
    ): Boolean {
        val response = ApiModule.retrofit.addComment(
            request = AddCommentRequest(
                userId = userId,
                advertId = advertId,
                text = text,
                pictureId = pictureId,
                location = location
            )
        )
        if (!response.isSuccessful) {
            throw IOException("Cannot add comment")
        }
        //since the response was successful the network must be available
        getComments(advertId)
        getAdvertInfo(advertId)
        return true
    }

    fun setImageDir(imageDir: File?) {
        this.imageDir = imageDir
    }
}