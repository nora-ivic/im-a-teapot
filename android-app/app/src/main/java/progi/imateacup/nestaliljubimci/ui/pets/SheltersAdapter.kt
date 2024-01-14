package progi.imateacup.nestaliljubimci.ui.pets

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import progi.imateacup.nestaliljubimci.databinding.SheltersCardBinding
import progi.imateacup.nestaliljubimci.model.networking.response.Pet
import progi.imateacup.nestaliljubimci.model.networking.response.ShelterResponse

class SheltersAdapter(
    private var shelters: List<ShelterResponse>, private val onShelterClickCallback: (ShelterResponse) -> Unit
) : RecyclerView.Adapter<SheltersAdapter.SheltersViewHolder>() {

    inner class SheltersViewHolder(private val binding: SheltersCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(shelter: ShelterResponse) {
            for(sheltera in shelters) {
                Log.d("SheltersAdapter", sheltera.name)
            }
            with (binding) {
                Log.d("SheltersAdapter", "bind: ${shelter.name}")
                binding.shelterCard.setOnClickListener {
                    onShelterClickCallback.invoke(shelter)
                }
                shelterName.text = shelter.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheltersViewHolder {
        val binding =
            SheltersCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SheltersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SheltersViewHolder, position: Int) {
        Log.d("SheltersAdapter", "onBindViewHolder: ${shelters[position].name}")
        holder.bind(shelters[position])
    }

    override fun getItemCount(): Int {
        Log.d("SheltersAdapter", "getItemCount: ${shelters.size}")
        return shelters.size
    }

    fun setShelters(shelters: List<ShelterResponse>) {
        this.shelters = shelters
        notifyDataSetChanged()
    }

}
