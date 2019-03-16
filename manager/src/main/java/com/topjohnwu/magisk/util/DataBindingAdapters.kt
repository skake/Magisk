package com.topjohnwu.magisk.util

import android.animation.ObjectAnimator
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
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.skoumal.teanity.databinding.applyTransformation
import com.skoumal.teanity.extensions.startEndToLeftRight
import com.skoumal.teanity.extensions.toPx
import com.skoumal.teanity.util.KItemDecoration
import com.topjohnwu.magisk.GlideApp
import com.topjohnwu.magisk.R
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

@BindingAdapter("invisibleTY", "invisibleTYMaxAlpha", requireAll = false)
fun setInvisibleWithTranslationY(view: View, isInvisible: Boolean, _maxAlpha: Float) {
    val maxAlpha = if (_maxAlpha == 0f) 1f else _maxAlpha
    val viewHeight =
        if (view.measuredHeight == 0)
            view.resources.getDimension(R.dimen.supplemental_animation_height).roundToInt()
        else view.measuredHeight
    val maxTranslationY = -viewHeight / 2f

    if (isInvisible) {
        view.animate()
            .translationY(maxTranslationY)
            .alpha(0f)
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(500)
            .setListener {
                onAnimationEnd {
                    view.isInvisible = true
                }
            }
            .start()
    } else {
        view.animate()
            .translationY(0f)
            .alpha(maxAlpha)
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(500)
            .setListener {
                onAnimationStart {
                    view.translationY = maxTranslationY
                    view.isInvisible = false
                }
            }
            .start()
    }

}

@BindingAdapter("pulse")
fun setPulse(view: View, shouldPulse: Boolean) {
    if (!shouldPulse) {
        view.clearAnimation()
        return
    }

    ObjectAnimator.ofFloat(view, View.ALPHA, 1f, .6f)
        .apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            duration = 1000
            interpolator = FastOutSlowInInterpolator()
        }
        .start()
}