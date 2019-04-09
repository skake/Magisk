package com.topjohnwu.magisk.ui.home

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.topjohnwu.magisk.Constants
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.FragmentHomeBinding
import com.topjohnwu.magisk.model.flash.MagiskInstallMethod
import com.topjohnwu.magisk.ui.base.MagiskFragment
import com.topjohnwu.magisk.ui.events.ViewEvent
import com.topjohnwu.magisk.util.addOnGlobalLayoutListener
import com.topjohnwu.magisk.util.chooser
import com.topjohnwu.magisk.util.setOnViewReadyListener
import com.topjohnwu.magisk.view.MagiskDialog
import org.koin.androidx.viewmodel.ext.viewModel
import kotlin.math.max

class HomeFragment : MagiskFragment<HomeViewModel, FragmentHomeBinding>() {

    override val layoutRes: Int = R.layout.fragment_home
    override val viewModel: HomeViewModel by viewModel()

    private val bottomSheet get() = BottomSheetBehavior.from(binding.homeSheet)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.ID.SELECT_BOOT -> viewModel.navigateToFlash(data?.data ?: return)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSheet()
        setUpSheetLayout()
    }

    override fun onSimpleEventDispatched(event: Int) {
        super.onSimpleEventDispatched(event)
        when (event) {
            ViewEvent.NAVIGATION_INSTALL_MAGISK -> showInstallMagiskDialog()
            ViewEvent.NAVIGATION_SELECT_BOOT -> fetchBoot()
        }
    }

    private fun showInstallMagiskDialog() {
        MagiskDialog(requireContext())
            .applyIcon(R.drawable.ic_magisk)
            .applyTitle("Install Magisk?")
            .applyMessage("Do you want to install Magisk now?")
            .applyButton(MagiskDialog.ButtonType.POSITIVE) {
                title = "Download Only"
                onClick { viewModel.installMagiskWithMethod(MagiskInstallMethod.DOWNLOAD_ONLY) }
            }
            .applyButton(MagiskDialog.ButtonType.NEUTRAL) {
                title = "Select and Patch a File"
                onClick { viewModel.installMagiskWithMethod(MagiskInstallMethod.PATCH) }
            }
            .applyButton(MagiskDialog.ButtonType.NEGATIVE) {
                title = "Direct Install (Recommended)"
                onClick { viewModel.installMagiskWithMethod(MagiskInstallMethod.INSTALL) }
            }
            .applyButton(MagiskDialog.ButtonType.IDGAF) {
                title = "Install to Inactive Slot (After OTA)"
                onClick { viewModel.installMagiskWithMethod(MagiskInstallMethod.INACTIVE_SLOT) }
            }
            .reveal()
    }

    private fun fetchBoot() {
        withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/*"
            }.chooser()
            startActivityForResult(intent, Constants.ID.SELECT_BOOT)
        }
    }

    private fun setUpSheet() {
        binding.homeParent.addOnGlobalLayoutListener {
            with(binding) {
                val offset = homeParent.measuredHeight - homeGlance.bottom
                bottomSheet.peekHeight = max(100, offset)
            }
        }
    }

    private fun setUpSheetLayout() {
        bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            init {
                binding.homeSheetInclude.sheetInternalLayout.setOnViewReadyListener {
                    onSlide(binding.homeSheet, 0f)
                }
            }

            override fun onSlide(view: View, fraction: Float) {
                with(binding.homeSheetInclude) {
                    sheetInternalLayout.translationY =
                        sheetAppbar.measuredHeight * (fraction - 1)
                }
            }

            override fun onStateChanged(view: View, newState: Int) = Unit
        })
    }

    override fun onBackPressed(): Boolean {
        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            binding.homeSheetInclude.sheetScroller.fullScroll(View.FOCUS_UP)
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            return true
        }
        return super.onBackPressed()
    }
}
