package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Pet(
    @SerialName("advert_id") val post_id: Int,
    @SerialName("advert_category") val post_category: String,
    @SerialName("pet_name") val PetName: String,
    @SerialName("username") val OwnerUsername: String,
    @SerialName("picture_link") val imageUri: String
)