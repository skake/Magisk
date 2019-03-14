package com.topjohnwu.magisk.util

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.isInvisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.skoumal.teanity.databinding.applyTransformation
import com.skoumal.teanity.extensions.startEndToLeftRight
import com.skoumal.teanity.extensions.toPx
import com.skoumal.teanity.util.KItemDecoration
import com.topjohnwu.magisk.GlideApp
import kotlin.math.roundToInt

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

@BindingAdapter("onNavigationClick")
fun setOnNavigationClickedListener(view: Toolbar, listener: View.OnClickListener) {
    view.setNavigationOnClickListener(listener)
}

@BindingAdapter("srcCompat")
fun setImageResource(view: AppCompatImageView, @DrawableRes resId: Int) {
    view.setImageResource(resId)
}

@BindingAdapter(
    "dividerColor",
    "dividerHorizontal",
    "dividerSize",
    "dividerAfterLast",
    "dividerMarginStart",
    "dividerMarginEnd",
    "dividerMarginTop",
    "dividerMarginBottom",
    requireAll = false
)
fun setDivider(
    view: RecyclerView,
    color: Int,
    horizontal: Boolean,
    _size: Float,
    _afterLast: Boolean?,
    marginStartF: Float,
    marginEndF: Float,
    marginTopF: Float,
    marginBottomF: Float
) {
    val orientation = if (horizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
    val size = if (_size > 0) _size.roundToInt() else 1.toPx()
    val (width, height) = if (horizontal) size to 1 else 1 to size
    val afterLast = _afterLast ?: true

    val marginStart = marginStartF.roundToInt()
    val marginEnd = marginEndF.roundToInt()
    val marginTop = marginTopF.roundToInt()
    val marginBottom = marginBottomF.roundToInt()
    val (marginLeft, marginRight) = view.context.startEndToLeftRight(marginStart, marginEnd)

    val drawable = GradientDrawable().apply {
        setSize(width, height)
        shape = GradientDrawable.RECTANGLE
        setColor(color)
    }.let {
        InsetDrawable(it, marginLeft, marginTop, marginRight, marginBottom)
    }

    val decoration = KItemDecoration(view.context, orientation)
        .setDeco(drawable)
        .apply { showAfterLast = afterLast }
    view.addItemDecoration(decoration)
}