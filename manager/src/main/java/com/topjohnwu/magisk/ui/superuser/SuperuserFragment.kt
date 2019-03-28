package com.topjohnwu.magisk.ui.superuser

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.FragmentSuperuserBinding
import com.topjohnwu.magisk.ui.base.MagiskFragment
import com.topjohnwu.magisk.util.addOnGlobalLayoutListener
import com.topjohnwu.magisk.util.setOnViewReadyListener
import org.koin.androidx.viewmodel.ext.viewModel
import kotlin.math.max

class SuperuserFragment : MagiskFragment<SuperuserViewModel, FragmentSuperuserBinding>() {

    override val layoutRes: Int = R.layout.fragment_superuser
    override val viewModel: SuperuserViewModel by viewModel()

    private val bottomSheet get() = BottomSheetBehavior.from(binding.superuserSheet)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSheet()
        setUpSheetLayout()
    }

    private fun setUpSheet() {
        binding.superuserParent.addOnGlobalLayoutListener {
            with(binding) {
                val offset = superuserParent.measuredHeight - superuserGlance.bottom
                bottomSheet.peekHeight = max(100, offset)
            }
        }
    }

    private fun setUpSheetLayout() {
        bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            init {
                binding.superuserSheetInclude.sheetInternalLayout.setOnViewReadyListener {
                    onSlide(binding.superuserSheet, 0f)
                }
            }

            override fun onSlide(view: View, fraction: Float) {
                with(binding.superuserSheetInclude) {
                    sheetInternalLayout.translationY =
                        sheetAppbar.measuredHeight * (fraction - 1)
                }
            }

            override fun onStateChanged(view: View, newState: Int) = Unit
        })

        binding.superuserSheetInclude.sheetRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                viewModel.isIdling.value = newState == RecyclerView.SCROLL_STATE_IDLE
            }
        })
    }

    override fun onBackPressed(): Boolean {
        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            binding.superuserSheetInclude.sheetRecycler.scrollToPosition(0)
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            return true
        }
        return super.onBackPressed()
    }
}
