package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CreateEditAdvertResponse (
    @SerialName("advert_id") val advertId: Int? = null
)