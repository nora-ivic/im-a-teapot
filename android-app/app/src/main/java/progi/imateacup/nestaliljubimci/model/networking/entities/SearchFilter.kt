package progi.imateacup.nestaliljubimci.model.networking.entities

import kotlinx.serialization.SerialName
@kotlinx.serialization.Serializable
data class SearchFilter (
    @SerialName("ime") val ime: String? = null,
    @SerialName("vrsta") val vrsta: String? = null,
    @SerialName("boja") val boja: String? = null,
    @SerialName("starost") val starost: Int? = null,
    @SerialName("datumNestanka") val datum_nestanka: String? = null,
    @SerialName("opis") val description: String? = null,
    @SerialName("lokacija") val lokacija: String? = null
)