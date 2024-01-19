package progi.imateacup.nestaliljubimci.ui.pets

import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.LoadingSpinnerBinding
import progi.imateacup.nestaliljubimci.databinding.MissingPetPostBinding
import progi.imateacup.nestaliljubimci.model.networking.response.Pet
import progi.imateacup.nestaliljubimci.util.MyRequestListener

const val IS_SHELTER = "IS_SHELTER"

class PetsAdapter(
    private var pets: List<Pet>,
    private val onPetPostClickCallback: (Pet) -> Unit,
    private val onEditPostClickCallback: (Pet) -> Unit,
    private val onDeletePostClickCallback: (Pet) -> Unit,
    private val onAddToShelterPostClickCallback: (Pet) -> Unit,
    private val sharedPreferences: SharedPreferences
) : RecyclerView.Adapter<PetsAdapter.MyViewHolder>() {
    private var showLoadingSpinner = true
    private var showingMyPets = false

    abstract inner class MyViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(pet: Pet?)
    }

    inner class PetsViewHolder(private val binding: ViewBinding) :
        MyViewHolder(binding) {
        override fun bind(pet: Pet?) {
            when (binding) {
                is MissingPetPostBinding -> {
                    with(binding) {
                        if (showingMyPets || sharedPreferences.getBoolean(
                                IS_SHELTER,
                                false
                            )
                        ) {
                            menuCard.visibility = View.VISIBLE
                            menuCard.bringToFront()
                            menuCard.setOnClickListener {
                                val popupMenu = PopupMenu(
                                    itemView.context,
                                    advertMenu
                                )
                                popupMenu.inflate(R.menu.advert_menu)
                                popupMenu.setForceShowIcon(true)
                                if (pet != null) {
                                    if (pet.ownerUsername != sharedPreferences.getString(
                                            "USERNAME",
                                            ""
                                        )) {
                                        popupMenu.menu.removeItem(R.id.delete_advert)
                                        popupMenu.menu.removeItem(R.id.edit_advert)
                                    } else {
                                        popupMenu.menu.removeItem(R.id.add_to_shelter)
                                    }
                                }
                                popupMenu.setOnMenuItemClickListener { item ->
                                    when (item.itemId) {
                                        R.id.edit_advert -> {
                                            onEditPostClickCallback.invoke(pet!!)
                                            true
                                        }

                                        R.id.delete_advert -> {
                                            onDeletePostClickCallback.invoke(pet!!)
                                            true
                                        }

                                        R.id.add_to_shelter -> {
                                            onAddToShelterPostClickCallback.invoke(pet!!)
                                            true
                                        }

                                        else -> false
                                    }
                                }
                                popupMenu.show()
                            }
                        } else {
                            menuCard.visibility = View.GONE
                        }

                        petPostCard.setOnClickListener {
                            onPetPostClickCallback.invoke(pet!!)
                        }
                        petName.text = pet!!.petName
                        OwnerUsername.text = pet.ownerUsername

                        Glide.with(itemView.context).load(pet.imageUri)
                            .placeholder(R.drawable.placeholder_image)
                            .listener(MyRequestListener(loadingSpinner)).into(petImage)
                    }
                }
            }
        }
    }

    inner class LoadingSpinnerViewHolder(private val binding: ViewBinding) :
        MyViewHolder(binding) {
        override fun bind(pet: Pet?) {
            when (binding) {
                is LoadingSpinnerBinding -> {
                    binding.postsLoadingProgressBar.visibility = if (showLoadingSpinner) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return when (viewType) {
            R.layout.loading_spinner -> LoadingSpinnerViewHolder(
                LoadingSpinnerBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> PetsViewHolder(
                MissingPetPostBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return pets.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            itemCount - 1 -> R.layout.loading_spinner
            else -> R.layout.missing_pet_post
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position < itemCount - 1) {
            holder.bind(pets[position])
        } else {
            holder.bind(null)
        }
    }

    fun updateData(newPetPosts: List<Pet>, showingMyPets: Boolean = false) {
        this.showingMyPets = showingMyPets
        pets = newPetPosts

        val handler = Handler(Looper.getMainLooper())
        handler.post {
            try {
                notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getPetsList(): List<Pet> {
        return pets
    }

    fun toggleLoadingSpinnerVisibility(fetchingPosts: Boolean) {
        showLoadingSpinner = fetchingPosts

        val handler = Handler(Looper.getMainLooper())
        handler.post {
            try {
                notifyItemChanged(itemCount - 1) // Notify the adapter about the last item change
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
