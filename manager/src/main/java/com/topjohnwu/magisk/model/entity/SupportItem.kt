package com.topjohnwu.magisk.model.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.skoumal.teanity.util.ComparableEntity
import com.topjohnwu.magisk.R


enum class SupportItem(
    @DrawableRes val icon: Int,
    @StringRes val title: Int
) : ComparableEntity<SupportItem> {

    PAYPAL(R.drawable.ic_paypal, R.string.paypal),
    PATREON(R.drawable.ic_patreon, R.string.patreon),
    TWITTER(R.drawable.ic_twitter, R.string.twitter),
    GITHUB(R.drawable.ic_github, R.string.github),
    XDA(R.drawable.ic_xda, R.string.xda);

    override fun contentSameAs(other: SupportItem) = sameAs(other)
    override fun sameAs(other: SupportItem) = icon == other.icon && title == other.title

}