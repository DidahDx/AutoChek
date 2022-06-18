package com.github.didahdx.autochek.common.extension

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.github.didahdx.autochek.R
import com.github.didahdx.autochek.ui.customViews.BadgeDrawable

/**
 * @author Daniel Didah on 6/13/22
 */

fun View.show(): View {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
    return this
}


fun View.hide(): View {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }

    return this
}

fun ImageView.loadImage(imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty() && imageUrl.endsWith("svg", true)) {
        val imageLoader = ImageLoader.Builder(this.context)
            .components { add(SvgDecoder.Factory()) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .data(imageUrl)
            .error(R.drawable.ic_error_image)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    } else {
        Glide.with(context)
            .load(imageUrl)
            .error(R.drawable.ic_error_image)
            .centerCrop()
            .into(this)
    }
}

fun MenuItem.createCartBadge(paramInt: Int, context: Context) {
    if (Build.VERSION.SDK_INT <= 15) {
        return
    }
    val cartItem: MenuItem = this
    val localLayerDrawable = cartItem.icon as LayerDrawable
    val cartBadgeDrawable = localLayerDrawable
        .findDrawableByLayerId(R.id.ic_badge)
    val badgeDrawable: BadgeDrawable
    if (cartBadgeDrawable != null
        && cartBadgeDrawable is BadgeDrawable
        && paramInt < 10
    ) {
        badgeDrawable = cartBadgeDrawable
    } else {
        badgeDrawable = BadgeDrawable(context)
    }
    badgeDrawable.setCount(paramInt)
    localLayerDrawable.mutate()
    localLayerDrawable.setDrawableByLayerId(R.id.ic_badge, badgeDrawable)
    cartItem.icon = localLayerDrawable
}