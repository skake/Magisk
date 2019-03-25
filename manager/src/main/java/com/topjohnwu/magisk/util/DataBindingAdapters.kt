package com.topjohnwu.magisk.util

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.TooltipCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.isInvisible
import androidx.databinding.BindingAdapter
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.bumptech.glide.request.RequestOptions
import com.skoumal.teanity.databinding.applyTransformation
import com.topjohnwu.magisk.GlideApp
import com.topjohnwu.magisk.R
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
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

@BindingAdapter("tooltipText")
fun setTooltip(view: View, tooltip: String) {
    TooltipCompat.setTooltipText(view, tooltip)
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

@BindingAdapter("alphaAnimated")
fun setAlphaAnimated(view: View, alpha: Float) {
    view.animate()
        .alpha(alpha)
        .setDuration(100)
        .setInterpolator(LinearInterpolator())
        .start()
}

@BindingAdapter("invisibleScale")
fun setInvisibleWithScale(view: View, isInvisible: Boolean) {
    view.animate()
        .scaleX(if (isInvisible) 0f else 1f)
        .scaleY(if (isInvisible) 0f else 1f)
        .setInterpolator(FastOutSlowInInterpolator())
        .start()
}

@BindingAdapter("movieBehavior", "movieBehaviorText")
fun setMovieBehavior(view: TextView, isMovieBehavior: Boolean, text: String) {
    (view.tag as? Disposable)?.dispose()
    if (isMovieBehavior) {
        val observer = Observable
            .interval(150, TimeUnit.MILLISECONDS)
            .assign {
                onNext { view.text = text.replaceRandomWithSpecial() }
            }
        view.tag = observer
    } else {
        view.text = text
    }
}