package progi.imateacup.nestaliljubimci.ui.pets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.MissingPetPostBinding
import progi.imateacup.nestaliljubimci.model.networking.response.Pet
import progi.imateacup.nestaliljubimci.util.MyRequestListener

class PetsAdapter(
    private var pets: List<Pet>, private val onShowClickCallback: (Pet) -> Unit
) : RecyclerView.Adapter<PetsAdapter.PetsViewHolder>() {

    inner class PetsViewHolder(private val binding: MissingPetPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pet: Pet) {
            with(binding) {
                petPostCard.setOnClickListener{
                    onShowClickCallback.invoke(pet)
                }
                petName.text = pet.petName
                OwnerUsername.text = pet.ownerUsername

                Glide
                    .with(itemView.context)
                    .load(pet.imageUri)
                    .placeholder(R.drawable.white_background)
                    .listener(MyRequestListener(loadingSpinner))
                    .into(petImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {
        val binding = MissingPetPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PetsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pets.size
    }

    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {
        holder.bind(pets[position])
    }

    fun updateData(newPetPosts: List<Pet>) {
        pets = newPetPosts
        notifyDataSetChanged()
    }

    fun getPetsList(): List<Pet> {
        return pets
    }
}
