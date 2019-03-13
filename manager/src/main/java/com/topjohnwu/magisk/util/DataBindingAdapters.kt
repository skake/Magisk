package com.topjohnwu.magisk.util

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.isInvisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.request.RequestOptions
import com.skoumal.teanity.databinding.applyTransformation
import com.topjohnwu.magisk.GlideApp

@BindingAdapter("url", "transformation", requireAll = false)
fun setImageFromUrl(view: ImageView, url: String?, transformation: Int) {
    if (url.isNullOrBlank()) {
        return
    }

    val options = RequestOptions().applyTransformation(transformation)

    GlideApp.with(view.context)
        .load(url)
        .apply(options)
        .into(view)
}

@BindingAdapter("invisible")
fun setGroupInvisible(group: Group, invisible: Boolean) {
    group.isInvisible = invisible
    group.updatePreLayout(group.parent as ConstraintLayout)
}

@BindingAdapter("invisibleUnless")
fun setGroupInvisibleUnless(group: Group, invisibleUnless: Boolean) {
    setGroupInvisible(group, invisibleUnless.not())
}
