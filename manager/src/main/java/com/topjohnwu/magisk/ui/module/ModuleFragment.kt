package com.topjohnwu.magisk.ui.module

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.FragmentModuleBinding
import com.topjohnwu.magisk.ui.base.MagiskFragment
import com.topjohnwu.magisk.util.addOnGlobalLayoutListener
import com.topjohnwu.magisk.util.setOnViewReadyListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.max


class ModuleFragment : MagiskFragment<ModuleViewModel, FragmentModuleBinding>() {

    override val layoutRes: Int = R.layout.fragment_module
    override val viewModel: ModuleViewModel by viewModel()

    private val bottomSheet get() = BottomSheetBehavior.from(binding.moduleSheet)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSheet()
        setUpSheetLayout()
        setUpGlance()
    }

    private fun setUpSheet() {
        binding.homeParent.addOnGlobalLayoutListener {
            with(binding) {
                val offset = homeParent.measuredHeight - moduleGlance.bottom
                bottomSheet.peekHeight = max(100, offset)
            }
        }
    }

    private fun setUpSheetLayout() {
        bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            init {
                binding.moduleSheetInclude.sheetInternalLayout.setOnViewReadyListener {
                    onSlide(binding.moduleSheet, 0f)
                }
            }

            override fun onSlide(view: View, fraction: Float) {
                with(binding.moduleSheetInclude) {
                    sheetInternalLayout.translationY =
                        sheetAppbar.measuredHeight * (fraction - 1)
                }
            }

            override fun onStateChanged(view: View, newState: Int) = Unit
        })
    }

    private fun setUpGlance() {
        GravitySnapHelper(Gravity.START).attachToRecyclerView(binding.moduleGlanceInstalled)
    }

    override fun onBackPressed(): Boolean {
        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            binding.moduleSheetInclude.sheetRecycler.scrollToPosition(0)
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            return true
        }
        return super.onBackPressed()
    }

}