package com.topjohnwu.magisk.model.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.skoumal.teanity.util.ComparableEntity
import com.topjohnwu.magisk.R


enum class SupportItem(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    val url: String
) : ComparableEntity<SupportItem> {

    PAYPAL(R.drawable.ic_paypal, R.string.paypal, "https://www.paypal.me/topjohnwu"),
    PATREON(R.drawable.ic_patreon, R.string.patreon, "https://www.patreon.com/topjohnwu"),
    TWITTER(R.drawable.ic_twitter, R.string.twitter, "https://twitter.com/topjohnwu"),
    GITHUB(R.drawable.ic_github, R.string.github, "https://github.com/topjohnwu/Magisk"),
    XDA(
        R.drawable.ic_xda,
        R.string.xda,
        "http://forum.xda-developers.com/showthread.php?t=3432382"
    );

    override fun contentSameAs(other: SupportItem) = sameAs(other)
    override fun sameAs(other: SupportItem) = icon == other.icon && title == other.title

}
