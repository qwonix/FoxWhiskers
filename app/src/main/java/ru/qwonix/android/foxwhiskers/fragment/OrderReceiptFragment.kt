package ru.qwonix.android.foxwhiskers.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentOrderReceiptBinding
import ru.qwonix.android.foxwhiskers.entity.Client
import ru.qwonix.android.foxwhiskers.entity.Order
import ru.qwonix.android.foxwhiskers.entity.OrderStatus
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.fragment.adapter.OrderReceiptAdapter
import ru.qwonix.android.foxwhiskers.fragment.adapter.PickUpLocationClickListener
import ru.qwonix.android.foxwhiskers.fragment.adapter.QrCodeClickListener
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler
import ru.qwonix.android.foxwhiskers.viewmodel.OrderViewModel


@AndroidEntryPoint
class OrderReceiptFragment : Fragment(R.layout.fragment_order_receipt) {

    companion object {

        @JvmStatic
        @BindingAdapter("backgroundTintBinding")
        fun backgroundTintBinding(view: View, @ColorRes colorId: Int) {
            view.backgroundTintList = ColorStateList.valueOf(view.resources.getColor(colorId))
        }
    }

    private val TAG = "OrderReceiptFragment"

    private val args: ProfileEditingFragmentArgs by navArgs()
    private lateinit var client: Client

    private val orderViewModel: OrderViewModel by viewModels()

    private lateinit var binding: FragmentOrderReceiptBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderReceiptBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            isLoading = true
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.client = args.client

        val orderReceiptAdapter = OrderReceiptAdapter()
        orderReceiptAdapter.qrCodeClickListener =
            object : QrCodeClickListener {
                override fun onQrClick(order: Order) {
                    if (order.status == OrderStatus.READY_FOR_PICKUP) {
                        QrBottomSheetDialogFragment(order.id).show(
                            parentFragmentManager,
                            "tag"
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Заказ ещё не готов к выдаче!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }

        orderReceiptAdapter.pickUpLocationClickListener =
            object : PickUpLocationClickListener {
                override fun onPickUpLocationClick(pickUpLocation: PickUpLocation) {
                    val mapIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("geo:${pickUpLocation.latitude}, ${pickUpLocation.longitude}?q=Усы+Лисы&z=18")
                    )
                    this@OrderReceiptFragment.startActivity(
                        Intent.createChooser(
                            mapIntent,
                            "Где построить маршрут?"
                        )
                    )
                }
            }

        binding.ordersRecycler.apply {
            adapter = orderReceiptAdapter
            val manager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            layoutManager = manager

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = 15
                }
            })
        }

        orderViewModel.orders.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")


                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful load client orders ${it.data}")
                    if (it.data.isEmpty()) {
                        binding.message = "Вы не оформили ни одного заказа :("
                    } else {
                        orderReceiptAdapter.setOrders(it.data)
                    }
                    binding.isLoading = false
                }
            }
        }

        orderViewModel.loadOrders(client.phoneNumber,
            object : CoroutinesErrorHandler {
                override fun onError(message: String) {
                    TODO("Not yet implemented")
                }
            })
    }
}