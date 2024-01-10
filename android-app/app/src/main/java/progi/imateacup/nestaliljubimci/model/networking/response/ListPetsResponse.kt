package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Pet(
    @SerialName("advert_id") val postId: Int,
    @SerialName("advert_category") val postCategory: String,
    @SerialName("pet_name") val petName: String,
    @SerialName("username") val ownerUsername: String,
    @SerialName("picture_link") val imageUri: String?
)