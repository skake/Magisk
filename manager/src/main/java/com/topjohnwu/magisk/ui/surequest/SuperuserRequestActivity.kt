package com.topjohnwu.magisk.ui.surequest

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.ActivitySuperuserRequestBinding
import com.topjohnwu.magisk.ui.base.MagiskActivity
import com.topjohnwu.magisk.view.MagiskDialog
import java.util.concurrent.TimeUnit
import org.koin.androidx.viewmodel.ext.android.viewModel as viewModelWorkGodDammit


class SuperuserRequestActivity :
    MagiskActivity<SuperuserRequestViewModel, ActivitySuperuserRequestBinding>() {

    override val layoutRes: Int = R.layout.activity_superuser_request
    override val viewModel: SuperuserRequestViewModel by viewModelWorkGodDammit()

    private lateinit var dialog: MagiskDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialog = MagiskDialog(this)
            .applyIcon(R.drawable.ic_root)
            .applyTitle(Html.fromHtml("Allow <b>Sample app</b> to access superuser rights?"))
            .applyMessage("Grants full access to your device.\nDeny if you're not sure!")
            .applyButton(MagiskDialog.ButtonType.POSITIVE) {
                title = "Grant"
                icon = R.drawable.ic_fingerprint
                isEnabled = false
            }
            .applyButton(MagiskDialog.ButtonType.NEGATIVE) {
                title = "Deny"
            }
            .onDismiss {
                finish()
            }
            .reveal()

        object : CountDownTimer(10000, 1000) {
            override fun onFinish() {
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {
                dialog.applyButton(MagiskDialog.ButtonType.NEGATIVE) {
                    title = "Deny (%d)".format(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished))
                }
            }
        }.start()
    }

    override fun finish() {
        if (this::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
        super.finish()
    }
}