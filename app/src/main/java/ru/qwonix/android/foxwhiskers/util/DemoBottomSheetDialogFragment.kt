package ru.qwonix.android.foxwhiskers.util

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.fragment.OderPickUpLocationFragment
import ru.qwonix.android.foxwhiskers.fragment.OrderConfirmationPaymentFragment

class DemoBottomSheetDialogFragment(
    private var startFragment: Fragment
) : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager
            .beginTransaction()
            .add(
                R.id.container, startFragment
            )
            .addToBackStack("fragment_root")
            .commit()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
            .also { dialog ->
                dialog.setOnShowListener {
                    val viewBehavior = BottomSheetBehavior.from(
                        dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
                    )
                    setupBehavior(viewBehavior)
                }
            }
    }

    private fun setupBehavior(bottomSheetBehavior: BottomSheetBehavior<View>) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.isDraggable = false
    }

    fun goToOrderPickUpLocationFragment() = transitToFragment(OderPickUpLocationFragment.newInstance())
    fun goToOrderConfirmationPaymentFragment() = transitToFragment(OrderConfirmationPaymentFragment.newInstance())

    private fun transitToFragment(newFragment: Fragment) {
        val currentFragmentRoot = childFragmentManager.fragments[0].requireView()

        childFragmentManager
            .beginTransaction()
            .apply {
                addSharedElement(currentFragmentRoot, currentFragmentRoot.transitionName)
                setReorderingAllowed(true)

                newFragment.sharedElementEnterTransition = BottomSheetSharedTransition()
            }
            .replace(R.id.container, newFragment)
            .addToBackStack(newFragment.javaClass.name)
            .commit()
    }

    fun goBack() {
        childFragmentManager.popBackStack()
    }
}

fun Fragment.withDemoBottomSheet(action: DemoBottomSheetDialogFragment.() -> Unit) {
    parentFragment
        ?.let { it as? DemoBottomSheetDialogFragment }
        ?.also(action)
}