package progi.imateacup.nestaliljubimci.model.networking.request.auth

import kotlinx.serialization.SerialName
import progi.imateacup.nestaliljubimci.model.networking.enums.AdvertisementCategory
import progi.imateacup.nestaliljubimci.model.networking.enums.PetSpecies

@kotlinx.serialization.Serializable
data class CreateEditAdvertRequest(
    @SerialName("advert_category") val advert_category: AdvertisementCategory,
    @SerialName("pet_name") val pet_name: String?,
    @SerialName("pet_species") val pet_species: PetSpecies?,
    @SerialName("pet_color") val pet_color: String?,
    @SerialName("pet_age") val pet_age: Int?,
    @SerialName("date_time_lost") val date_time_lost: String?,
    @SerialName("location_lost") val location_lost: String?,
    @SerialName("description") val description: String?,
    @SerialName("picture_links") val pictureLinks: List<String>,
    )