package progi.imateacup.nestaliljubimci.ui.advertDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import progi.imateacup.nestaliljubimci.model.networking.request.auth.AddCommentRequest
import progi.imateacup.nestaliljubimci.model.networking.entities.Comment
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
        viewModelScope.launch {
            try {
                val comments = fetchComments(advertId)
                _commentsLiveData.value = comments
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }
        }
    }

    private suspend fun fetchComments(advertId: Int): List<Comment> {
        val result = ApiModule.retrofit.getComments(advertId = advertId, page  = 1, items = 5)
        if (!result.isSuccessful) {
            throw IOException("Unable to get comments")
        }
        return result.body()!!.comments
    }

    fun advertComment(
        userId: Int,
        advertId: Int,
        text: String,
        pictureId: Int,
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
        userId: Int,
        advertId: Int,
        text: String,
        pictureId: Int,
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
        getAdvertDetails(advertId)
        return true
    }

    fun setImageDir(imageDir: File?) {
        this.imageDir = imageDir
    }
}