package progi.imateacup.nestaliljubimci.ui.advertDetails

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.ImageCardBinding
import progi.imateacup.nestaliljubimci.util.MyRequestListener

class ImagesAdapter(private var imagesList: List<String>) :
    RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(private var binding: ImageCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pictureLink: String) {
            with(binding) {

                Glide
                    .with(itemView.context)
                    .load(pictureLink)
                    .placeholder(R.drawable.white_background)
                    .listener(MyRequestListener(loadingSpinner))
                    .into(listItemImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val detailedViewBinding: ImageCardBinding =
            ImageCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ImageViewHolder(detailedViewBinding)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imagesList[position])
    }

    fun updateData(newImagesLinks: List<String>) {
        imagesList = newImagesLinks
        notifyDataSetChanged()
    }
}