package progi.imateacup.nestaliljubimci.model.networking.entities

import kotlinx.serialization.SerialName
@kotlinx.serialization.Serializable
data class SearchFilter (
    @SerialName("ime") val ime: String?,
    @SerialName("vrsta") val vrsta: String?,
    @SerialName("boja") val boja: String?,
    @SerialName("starost") val starost: String?,
    @SerialName("datumNestanka") val datum_nestanka: String?,
    @SerialName("opis") val description: String?,
    @SerialName("lokacija") val lokacija: String?
)