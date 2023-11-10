package progi.imateacup.nestaliljubimci.model.networking.entities

import kotlinx.serialization.SerialName
@kotlinx.serialization.Serializable
data class Advert(
    @SerialName("id") val id: String,
    @SerialName("category") val category: String,
    @SerialName("deleted") val deleted: Boolean,
    @SerialName("user_id") val userId: String,
    @SerialName("pet_id") val petId: String,
    @SerialName("date_time_adv") val dateTimeAdv: String,
    @SerialName("is_in_shelter") val isInShelter: Boolean,
    @SerialName("shelter_id") val shelterId: String,
)

