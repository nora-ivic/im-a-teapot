package progi.imateacup.nestaliljubimci.util

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MyRequestListener(private val loadingSpinner: View) : RequestListener<Drawable> {
    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        loadingSpinner.isVisible = false
        return false
    }

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        loadingSpinner.isVisible = false
        return false
    }
}
