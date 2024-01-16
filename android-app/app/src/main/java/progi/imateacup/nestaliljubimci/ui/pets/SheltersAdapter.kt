package progi.imateacup.nestaliljubimci.ui.pets

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import progi.imateacup.nestaliljubimci.databinding.SheltersCardBinding
import progi.imateacup.nestaliljubimci.model.networking.response.Pet
import progi.imateacup.nestaliljubimci.model.networking.response.ShelterResponse

class SheltersAdapter(
    private var shelters: List<ShelterResponse>
) : RecyclerView.Adapter<SheltersAdapter.SheltersViewHolder>() {

    inner class SheltersViewHolder(private val binding: SheltersCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(shelter: ShelterResponse) {
            with(binding) {
                shelterName.text = shelter.name
                shelterEmail.text = shelter.email
                shelterPhoneNumber.text = shelter.phone_number
                shelterUsername.text = shelter.username
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheltersViewHolder {
        val binding =
            SheltersCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SheltersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SheltersViewHolder, position: Int) {
        holder.bind(shelters[position])
    }

    override fun getItemCount(): Int {
        return shelters.size
    }

    fun setShelters(shelters: List<ShelterResponse>) {
        this.shelters = shelters
        notifyDataSetChanged()
    }

}
