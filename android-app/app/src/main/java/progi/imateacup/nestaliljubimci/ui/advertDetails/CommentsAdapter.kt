package progi.imateacup.nestaliljubimci.ui.advertDetails

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import progi.imateacup.nestaliljubimci.databinding.CommentCardBinding
import progi.imateacup.nestaliljubimci.model.networking.entities.Comment

class CommentsAdapter(private var commentList: List<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.AdvertCommentsViewHolder>() {

    private var showLoadingSpinner = true

    inner class AdvertCommentsViewHolder(private var binding: CommentCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            with(binding) {
                username.text = comment.username
                commentText.text = comment.text
                phone.text = comment.userPhoneNumber
                email.text = comment.userEmail
                locationIcon.visibility = if (comment.location == "") {
                    ViewGroup.GONE
                } else {
                    ViewGroup.VISIBLE
                }

                val direction =
                    AdvertDetailsFragmentDirections.actionAdvertDetailsViewToMapDisplayLocationFragment(
                        comment.location
                    )
                locationIcon.setOnClickListener { view ->
                    view.findNavController().navigate(direction)
                }

                imageIcon.visibility = if (comment.pictureLinks.isEmpty()) {
                    ViewGroup.GONE
                } else {
                    ViewGroup.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertCommentsViewHolder {
        val commentBinding: CommentCardBinding =
            CommentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return AdvertCommentsViewHolder(commentBinding)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: AdvertCommentsViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    fun getCommentList(): List<Comment> {
        return commentList
    }

    fun updateData(newComments: List<Comment>) {
        commentList = newComments
        notifyDataSetChanged()
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