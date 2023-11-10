package progi.imateacup.nestaliljubimci.ui.detailed_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import progi.imateacup.nestaliljubimci.databinding.CommentCardBinding
import progi.imateacup.nestaliljubimci.model.networking.entities.Comment

class DetailedViewAdapter(private var commentList: List<Comment>) : RecyclerView.Adapter<DetailedViewAdapter.ShowReviewsViewHolder>() {

    inner class ShowReviewsViewHolder(private var binding: CommentCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowReviewsViewHolder {
        val commentBinding: CommentCardBinding = CommentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ShowReviewsViewHolder(commentBinding)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: ShowReviewsViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    fun updateData(newRevies: List<Comment>) {
        commentList = newRevies
        notifyDataSetChanged()
    }
}