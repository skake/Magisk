package com.topjohnwu.magisk.ui.flash

import android.content.Context
import android.os.Handler
import androidx.core.net.toUri
import androidx.core.os.postDelayed
import com.skoumal.teanity.databinding.ComparableRvItem
import com.skoumal.teanity.extensions.ui
import com.skoumal.teanity.util.DiffObservableList
import com.skoumal.teanity.util.KObservableField
import com.topjohnwu.magisk.BR
import com.topjohnwu.magisk.Constants
import com.topjohnwu.magisk.model.entity.ConsoleRvItem
import com.topjohnwu.magisk.model.flash.FlashManager
import com.topjohnwu.magisk.ui.base.MagiskViewModel
import com.topjohnwu.magisk.ui.events.ViewEvent
import com.topjohnwu.magisk.util.*
import com.topjohnwu.magisk.util.directions.FlashAction
import io.reactivex.Single
import me.tatarka.bindingcollectionadapter2.OnItemBind
import java.io.File
import java.util.concurrent.TimeUnit


class FlashViewModel(
    data: FlashActivityArgs,
    private val context: Context
) : MagiskViewModel(), IFlashLog {

    val showRestartTitle = KObservableField(false)
    val behaviorText = KObservableField("Flashing...")

    val lines = DiffObservableList(ComparableRvItem.callback)
    val itemBinding = OnItemBind<ComparableRvItem<*>> { itemBinding, _, item ->
        item.bind(itemBinding)
        itemBinding.bindExtra(BR.viewModel, this@FlashViewModel)
    }

    init {
        val job = when (data.action) {
            FlashAction.FLASH_ZIP -> FlashManager<FlashManager.Flash> {
                context = this@FlashViewModel.context
                console = this@FlashViewModel
                source = data.data.orEmpty().toUri()
            }
            FlashAction.FLASH_MAGISK -> FlashManager<FlashManager.Magisk> {
                context = this@FlashViewModel.context
                console = this@FlashViewModel
            }
            FlashAction.FLASH_INACTIVE_SLOT -> FlashManager<FlashManager.InactiveSlot> {
                context = this@FlashViewModel.context
                console = this@FlashViewModel
            }
            FlashAction.PATCH_BOOT -> FlashManager<FlashManager.PatchBoot> {
                context = this@FlashViewModel.context
                console = this@FlashViewModel
                source = data.data.orEmpty().toUri()
            }
            FlashAction.UNINSTALL -> FlashManager<FlashManager.Uninstall> {
                context = this@FlashViewModel.context
                console = this@FlashViewModel
                source = data.data.orEmpty().toUri()
            }
        }

        job.delay(3, TimeUnit.SECONDS)
            .doOnError { it.printStackTrace() }
            .assign {
                onSuccess { showJobSuccess() }
                onError { showJobFailure() }
            }
    }

    //region UI actions
    fun backPressed() = ViewEvent.BACK_PRESS.publish()

    fun restartPressed() = reboot()

    fun savePressed() = Single.just(now)
        .map { it.toTime(timeFormatFull) }
        .map { Constants.MAGISK_LOG_FILENAME.format(it) }
        .map { File(Constants.EXTERNAL_PATH, it) }
        .map { file ->
            val log = lines.filterIsInstance<ConsoleRvItem>()
                .joinToString("\n") { it.text }
            file.writeText(log)
            file.path
        }
        .assign {
            onSuccess { /*show snackbar or something*/ }
            onError { /*show snackbar*/ }
        }
    //endregion

    override fun log(line: String) {
        ui { lines.add(ConsoleRvItem(line)) }
    }

    private fun showJobSuccess() {
        state = State.LOADED
        behaviorText.value = "Done!"

        log("- All done!")

        Handler().postDelayed(500) {
            showRestartTitle.value = true
        }
    }

    private fun showJobFailure() {
        state = State.LOADING_FAILED
        behaviorText.value = "Failed :("

        log("- Flashing failed horribly")
    }

}
