package progi.imateacup.nestaliljubimci.model.networking.entities

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SearchFilter(
    @SerialName("pet_name") val ime: String? = null,
    @SerialName("pet_species") val vrsta: String? = null,
    @SerialName("pet_color") val boja: String? = null,
    @SerialName("pet_age") val starost: String? = null,
    @SerialName("date_time_lost") val datumNestanka: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("location_lost") val lokacija: String? = null,
    @SerialName("username") val korisnickoIme: String? = null,
    @SerialName("shelter_name") val imeSklonista: String? = null,
    @SerialName("advert_category") val kategorijaOglasa: String? = null
)

//Extension function
fun SearchFilter.toQueryMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    map["pet_name"] = ime ?: ""
    map["pet_species"] = vrsta ?: ""
    map["pet_color"] = boja ?: ""
    map["pet_age"] = starost ?: ""
    map["date_time_lost"] = datumNestanka ?: ""
    map["description"] = description ?: ""
    map["location_lost"] = lokacija ?: ""
    map["username"] = korisnickoIme ?: ""
    map["shelter_name"] = imeSklonista ?: ""
    map["advert_category"] = kategorijaOglasa ?: ""
    return map
}