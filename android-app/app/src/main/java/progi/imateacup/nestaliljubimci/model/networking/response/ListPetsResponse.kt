package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ListPetsResponse(
    @SerialName("pets") val pets: List<Pet>
)

@kotlinx.serialization.Serializable
data class Pet(
    @SerialName("temp") val temp: String
    //TODO
    // implement correct fields
)