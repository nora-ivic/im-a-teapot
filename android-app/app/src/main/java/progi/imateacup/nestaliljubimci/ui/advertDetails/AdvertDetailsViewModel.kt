package progi.imateacup.nestaliljubimci.ui.advertDetails

import android.net.Uri
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import progi.imateacup.nestaliljubimci.model.networking.request.auth.AddCommentRequest
import progi.imateacup.nestaliljubimci.model.networking.entities.Comment
import progi.imateacup.nestaliljubimci.model.networking.enums.DisplayState
import progi.imateacup.nestaliljubimci.model.networking.response.Advert
import progi.imateacup.nestaliljubimci.networking.ApiModule
import progi.imateacup.nestaliljubimci.ui.pets.PetsViewModel
import java.io.File
import java.io.IOException

class AdvertDetailsViewModel : ViewModel() {

    private val _commentsLiveData = MutableLiveData<List<Comment>>()
    val commentsLiveData: LiveData<List<Comment>> = _commentsLiveData

    private val _advertLiveData = MutableLiveData<Advert?>()
    val advertLiveData: LiveData<Advert?> = _advertLiveData

    private val _commentAddedLiveData = MutableLiveData<Boolean>()
    val commentAddedLiveData: LiveData<Boolean> = _commentAddedLiveData

    private val _advertFetchSuccessLiveData = MutableLiveData<Boolean>()
    val advertFetchSuccessLiveData: LiveData<Boolean> = _advertFetchSuccessLiveData

    private val _commentsDisplayStateLiveData = MutableLiveData<DisplayState>()
    val commentsDisplayStateLiveData: LiveData<DisplayState> = _commentsDisplayStateLiveData

    private val _messageCoordinatesLiveData = MutableLiveData<String?>()
    val messageCoordinatesLiveData: LiveData<String?> = _messageCoordinatesLiveData

    private val _imageLinkLiveData = MutableLiveData<Uri?>()
    val imageLinkLiveData: LiveData<Uri?> = _imageLinkLiveData

    private val _imageUploadSuccessLiveData = MutableLiveData<Boolean>()
    val imageUploadSuccessLiveData: LiveData<Boolean> = _imageUploadSuccessLiveData

    private var fetching = false
    private var page = 0

    private val _accessTokenExpiredLiveData = MutableLiveData<Boolean>()
    val accessTokenExpiredLiveData: LiveData<Boolean> = _accessTokenExpiredLiveData

    private var imageDir: File? = null
    fun getAdvertDetails(advertId: Int) {
        viewModelScope.launch {
            try {
                val advert = fetchAdvertDetails(advertId)
                if (advert?.petName == "?")
                    advert.petName = null
                _advertLiveData.value = advert
                _advertFetchSuccessLiveData.value = true
            } catch (err: Exception) {
                if (err.localizedMessage == "Unauthorized") {
                    _accessTokenExpiredLiveData.value = true
                }
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

    fun getComments(advertId: Int) {
        if (fetching) {
            return
        }
        _commentsDisplayStateLiveData.value = DisplayState.LOADING
        fetching = true
        page++

        viewModelScope.launch {
            try {
                val newComments = fetchComments(advertId)
                if (_commentsLiveData.value == null) {
                    _commentsLiveData.value = listOf()
                }
                val oldComments = _commentsLiveData.value
                if (!newComments.isNullOrEmpty()) {
                    _commentsLiveData.value = oldComments!! + newComments
                    _commentsDisplayStateLiveData.value = DisplayState.SUCCESSGET

                } else {
                    if (oldComments!!.isNotEmpty()) {
                        _commentsDisplayStateLiveData.value = DisplayState.SUCCESSGET
                    } else {
                        _commentsDisplayStateLiveData.value = DisplayState.NOPOSTS
                    }
                }
                fetching = false
            } catch (err: Exception) {
                _commentsDisplayStateLiveData.value = DisplayState.ERRORGET
                fetching = false
            }
        }
    }

    fun uploadImage(picture: File) {
        viewModelScope.launch {
            try {
                val link = (ApiModule.BASE_URL.removeSuffix("/") + postImageRequest(picture))
                _imageLinkLiveData.value = Uri.parse(link)
                _imageUploadSuccessLiveData.value = true
            } catch (err: Exception) {
                if (err.localizedMessage == "Unauthorized") {
                    _accessTokenExpiredLiveData.value = true
                }
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

    private suspend fun fetchComments(advertId: Int): List<Comment>? {
        val result = ApiModule.retrofit.getComments(advertId = advertId, page = page, items = 5)
        if (result.code() == 401) {
            throw IOException("Unauthorized")
        }
        if (!result.isSuccessful) {
            throw IOException("Unable to get comments")
        } else {
            return result.body()
        }
    }

    fun advertComment(
        advertId: Int,
        text: String,
        pictureLinks: List<String>,
        location: String
    ) {
        viewModelScope.launch {
            try {
                _commentAddedLiveData.value =
                    postComment(advertId, text, pictureLinks, location)

            } catch (err: Exception) {
                if (err.localizedMessage == "Unauthorized") {
                    _accessTokenExpiredLiveData.value = true
                }
                _commentAddedLiveData.value = false
            }
        }
    }

    private suspend fun postComment(
        advertId: Int,
        text: String,
        pictureLinks: List<String>,
        location: String
    ): Boolean {
        val response = ApiModule.retrofit.addComment(
            advertId = advertId,
            request = AddCommentRequest(
                text = text,
                pictureLinks = pictureLinks,
                location = location
            )
        )
        if (response.code() == 401) {
            throw IOException("Unauthorized")
        }
        if (!response.isSuccessful) {
            throw IOException("Cannot add comment")
        }
        _commentsLiveData.value = emptyList()
        page = 0
        getComments(advertId)
        return true
    }

    fun setImageDir(imageDir: File?) {
        this.imageDir = imageDir
    }

    fun setMessageCoordinates(coordinates: String?) {
        _messageCoordinatesLiveData.value = coordinates
    }
    fun setImageLink(link: Uri?) {
        _imageLinkLiveData.value = link
    }
}