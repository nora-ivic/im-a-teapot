package progi.imateacup.nestaliljubimci.ui.advertDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import progi.imateacup.nestaliljubimci.model.networking.request.auth.AddCommentRequest
import progi.imateacup.nestaliljubimci.model.networking.entities.Comment
import progi.imateacup.nestaliljubimci.model.networking.enums.PetsDisplayState
import progi.imateacup.nestaliljubimci.model.networking.response.Advert
import progi.imateacup.nestaliljubimci.networking.ApiModule
import java.io.File
import java.io.IOException

class AdvertDetailsViewModel : ViewModel() {

    private val _commentsLiveData = MutableLiveData<List<Comment>>()
    val commentsLiveData: LiveData<List<Comment>> = _commentsLiveData

    private val _advertLiveData = MutableLiveData<Advert>()
    val advertLiveData: LiveData<Advert> = _advertLiveData

    private val _commentAddedLiveData = MutableLiveData<Boolean>()
    val commentAddedLiveData: LiveData<Boolean> = _commentAddedLiveData

    private val _advertFetchSuccessLiveData = MutableLiveData<Boolean>()
    val advertFetchSuccessLiveData: LiveData<Boolean> = _advertFetchSuccessLiveData

    private val _commentsDisplayStateLiveData = MutableLiveData<PetsDisplayState>()
    val commentsDisplayStateLiveData: LiveData<PetsDisplayState> = _commentsDisplayStateLiveData

    private var fetching = false
    private var page = 0

    private var imageDir: File? = null
    fun getAdvertDetails(advertId: Int) {
        viewModelScope.launch {
            try {
                _advertLiveData.value = fetchAdvertDetails(advertId)
                _advertFetchSuccessLiveData.value = true
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
                _advertFetchSuccessLiveData.value = false
            }
        }
    }

    private suspend fun fetchAdvertDetails(advertId: Int): Advert? {
        val response = ApiModule.retrofit.getAdvertDetails(advertId = advertId)

        if (!response.isSuccessful)
            throw IOException("Failed to get advert details")
        else
            return response.body()
    }

    fun getComments(advertId: Int) {
        if (fetching) {
            return
        }
        _commentsDisplayStateLiveData.value = PetsDisplayState.LOADING
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
                    _commentsDisplayStateLiveData.value = PetsDisplayState.SUCCESS

                } else {
                    if (oldComments!!.isNotEmpty()) {
                        _commentsDisplayStateLiveData.value = PetsDisplayState.SUCCESS
                    } else {
                        _commentsDisplayStateLiveData.value = PetsDisplayState.NOPOSTS
                    }
                }
                fetching = false
            } catch (err: Exception) {
                _commentsDisplayStateLiveData.value = PetsDisplayState.ERROR
                fetching = false
            }
        }
    }

    private suspend fun fetchComments(advertId: Int): List<Comment>? {
        val result = ApiModule.retrofit.getComments(advertId = advertId, page = page, items = 5)
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
                Log.e("EXCEPTION", err.toString())
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
        if (!response.isSuccessful) {
            throw IOException("Cannot add comment")
        }
        getAdvertDetails(advertId)
        return true
    }

    fun setImageDir(imageDir: File?) {
        this.imageDir = imageDir
    }


}