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
    ime?.takeIf { it.isNotBlank() }?.let { map["pet_name"] = it }
    vrsta?.takeIf { it.isNotBlank() }?.let { map["pet_species"] = it }
    boja?.takeIf { it.isNotBlank() }?.let { map["pet_color"] = it }
    starost?.takeIf { it.isNotBlank() }?.let { map["pet_age"] = it }
    datumNestanka?.takeIf { it.isNotBlank() }?.let { map["date_time_lost"] = it }
    description?.takeIf { it.isNotBlank() }?.let { map["description"] = it }
    lokacija?.takeIf { it.isNotBlank() }?.let { map["location_lost"] = it }
    korisnickoIme?.takeIf { it.isNotBlank() }?.let { map["username"] = it }
    imeSklonista?.takeIf { it.isNotBlank() }?.let { map["shelter_name"] = it }
    kategorijaOglasa?.takeIf { it.isNotBlank() }?.let { map["advert_category"] = it }
    return map
}