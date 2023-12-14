package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName
import progi.imateacup.nestaliljubimci.model.networking.enums.AdvertisementCategory
import progi.imateacup.nestaliljubimci.model.networking.enums.PetSpecies

@kotlinx.serialization.Serializable
data class Advert(
    @SerialName("advert_id") val id: Int,
    @SerialName("advert_category") val category: AdvertisementCategory,
    @SerialName("pet_name") val petName: String?,
    @SerialName("pet_species") val petSpecies: PetSpecies?,
    @SerialName("pet_color") val petColor: String?,
    @SerialName("pet_age") val petAge: Int?,
    @SerialName("date_time_lost") val dateTimeLost: String?,
    @SerialName("location_lost") val locationLost: String?,
    @SerialName("description") val description: String?,
    @SerialName("is_in_shelter") val isInShelter: Boolean,
    @SerialName("username") val username: String,
    @SerialName("picture_links") val pictureLinks: List<String>,
    @SerialName("shelter_name") val shelterName: String?,
    @SerialName("shelter_username") val shelterUsername: String?,
    @SerialName("shelter_email") val shelterEmail: String?,
    @SerialName("shelter_phone_number") val shelterPhone: String?,
    )