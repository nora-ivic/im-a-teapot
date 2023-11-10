package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName
import progi.imateacup.nestaliljubimci.model.networking.entities.Advert

@kotlinx.serialization.Serializable
data class AdvertResponse(
    @SerialName("add") val advert: Advert
)