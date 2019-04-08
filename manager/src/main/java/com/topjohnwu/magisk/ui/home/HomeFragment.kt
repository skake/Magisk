package com.topjohnwu.magisk.ui.home

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.FragmentHomeBinding
import com.topjohnwu.magisk.ui.base.MagiskFragment
import com.topjohnwu.magisk.util.addOnGlobalLayoutListener
import com.topjohnwu.magisk.util.setOnViewReadyListener
import org.koin.androidx.viewmodel.ext.viewModel
import kotlin.math.max

class HomeFragment : MagiskFragment<HomeViewModel, FragmentHomeBinding>() {

    override val layoutRes: Int = R.layout.fragment_home
    override val viewModel: HomeViewModel by viewModel()

    private val bottomSheet get() = BottomSheetBehavior.from(binding.homeSheet)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSheet()
        setUpSheetLayout()
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
