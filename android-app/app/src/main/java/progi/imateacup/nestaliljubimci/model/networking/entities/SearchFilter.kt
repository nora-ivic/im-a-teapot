package progi.imateacup.nestaliljubimci.model.networking.entities

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SearchFilter(
    @SerialName("ime") val ime: String? = null,
    @SerialName("vrsta") val vrsta: String? = null,
    @SerialName("boja") val boja: String? = null,
    @SerialName("starost") val starost: String? = null,
    @SerialName("datumNestanka") val datumNestanka: String? = null,
    @SerialName("opis") val description: String? = null,
    @SerialName("lokacija") val lokacija: String? = null,
    @SerialName("korisnickoIme") val korisnickoIme: String? = null,
    @SerialName("imeSklonista") val imeSklonista: String? = null,
    @SerialName("kategorijaOglasa") val kategorijaOglasa: String? = null
)

//Extension function
fun SearchFilter.toQueryMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    map["ime"] = ime ?: ""
    map["vrsta"] = vrsta ?: ""
    map["boja"] = boja ?: ""
    map["starost"] = starost ?: ""
    map["datumNestanka"] = datumNestanka ?: ""
    map["opis"] = description ?: ""
    map["lokacija"] = lokacija ?: ""
    map["korisnickoIme"] = korisnickoIme ?: ""
    map["imeSklonista"] = imeSklonista ?: ""
    map["kategorijaOglasa"] = kategorijaOglasa ?: ""
    return map
}