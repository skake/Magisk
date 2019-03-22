package com.topjohnwu.magisk.ui.log

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.FragmentLogBinding
import com.topjohnwu.magisk.ui.base.MagiskFragment
import com.topjohnwu.magisk.util.addOnGlobalLayoutListener
import com.topjohnwu.magisk.util.setOnViewReadyListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.max


class LogFragment : MagiskFragment<LogViewModel, FragmentLogBinding>() {

    override val layoutRes: Int = R.layout.fragment_log
    override val viewModel: LogViewModel by viewModel()

    private val bottomSheet get() = BottomSheetBehavior.from(binding.logSheet)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSheet()
        setUpSheetLayout()
        setUpGlance()
    }

    private fun setUpSheet() {
        binding.logParent.addOnGlobalLayoutListener {
            with(binding) {
                val offset = logParent.measuredHeight - logGlance.bottom
                bottomSheet.peekHeight = max(100, offset)
            }
        }
    }

    private fun setUpSheetLayout() {
        bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            init {
                binding.logSheetInclude.sheetInternalLayout.setOnViewReadyListener {
                    onSlide(binding.logSheet, 0f)
                }
            }

            override fun onSlide(view: View, fraction: Float) {
                with(binding.logSheetInclude) {
                    sheetInternalLayout.translationY =
                        sheetAppbar.measuredHeight * (fraction - 1)
                }
            }

            override fun onStateChanged(view: View, newState: Int) = Unit
        })
    }

    private fun setUpGlance() {
        binding.logGlanceTabs.setupWithViewPager(binding.logSheetInclude.sheetPager)
    }

    override fun onBackPressed(): Boolean {
        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            //binding.logSheetInclude.sheetRecycler.scrollToPosition(0)
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            return true
        }
        return super.onBackPressed()
    }

}