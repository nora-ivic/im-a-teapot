package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ListPetsResponse(
    @SerialName("pets") val pets: List<Pet>
)

@kotlinx.serialization.Serializable
data class Pet(
    /*TODO
    *  zamijeni serial names sa ispravnima
    *  nakon sto se definira api endpoint
    * */
    @SerialName("img") val imageUri: String,
    @SerialName("petname") val PetName: String,
    @SerialName("username") val OwnerUsername: String
)