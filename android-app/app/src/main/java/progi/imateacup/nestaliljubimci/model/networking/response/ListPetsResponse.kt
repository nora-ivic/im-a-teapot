package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ListPetsResponse(
    @SerialName("pets") val pets: List<Pet>
)

@kotlinx.serialization.Serializable
data class Pet(
    @SerialName("img") val imageUri: String,
    @SerialName("petname") val PetName: String,
    @SerialName("username") val OwnerUsername: String
)