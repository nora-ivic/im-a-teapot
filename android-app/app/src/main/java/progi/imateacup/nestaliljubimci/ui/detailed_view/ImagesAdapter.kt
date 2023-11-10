package progi.imateacup.nestaliljubimci.ui.detailed_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import progi.imateacup.nestaliljubimci.databinding.FragmentDetailedViewBinding
import progi.imateacup.nestaliljubimci.model.networking.entities.Picture

class ImagesAdapter(private var imagesList: List<Picture>) : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(private var binding: FragmentDetailedViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(picture: Picture) {
            with(binding) {
                /*
                    profileName.text = comment.user.email.substring(0, comment.user.email.indexOf("@"))
                    commentComment.text = comment.comment
                    Glide
                        .with(itemView.context)
                        .load(comment.user.imageUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .into(profilePicture)
                 */
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val detailedViewBinding: FragmentDetailedViewBinding = FragmentDetailedViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ImageViewHolder(detailedViewBinding)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imagesList[position])
    }

    fun updateData(newPicture: List<Picture>) {
        imagesList = newPicture
        notifyDataSetChanged()
    }
}