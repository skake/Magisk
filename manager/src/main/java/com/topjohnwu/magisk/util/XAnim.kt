package com.topjohnwu.magisk.util

import android.animation.Animator
import android.view.ViewPropertyAnimator


fun ViewPropertyAnimator.setListener(builder: ViewPropertyAnimatorListenerBuilder.() -> Unit) =
    setListener(ViewPropertyAnimatorListenerBuilder().apply(builder).build())

typealias AnimatorListener = (Animator?) -> Unit

class ViewPropertyAnimatorListenerBuilder {

    private var internalOnAnimationRepeat: AnimatorListener = {}
    private var internalOnAnimationEnd: AnimatorListener = {}
    private var internalOnAnimationCancel: AnimatorListener = {}
    private var internalOnAnimationStart: AnimatorListener = {}

    fun onAnimationRepeat(listener: AnimatorListener) {
        internalOnAnimationRepeat = listener
    }

    fun onAnimationEnd(listener: AnimatorListener) {
        internalOnAnimationEnd = listener
    }

    fun onAnimationCancel(listener: AnimatorListener) {
        internalOnAnimationCancel = listener
    }

    fun onAnimationStart(listener: AnimatorListener) {
        internalOnAnimationStart = listener
    }

    internal fun build(): Animator.AnimatorListener {
        return object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) =
                internalOnAnimationRepeat(animation)

            override fun onAnimationEnd(animation: Animator?) =
                internalOnAnimationEnd(animation)

            override fun onAnimationCancel(animation: Animator?) =
                internalOnAnimationCancel(animation)

            override fun onAnimationStart(animation: Animator?) =
                internalOnAnimationStart(animation)
        }
    }
}