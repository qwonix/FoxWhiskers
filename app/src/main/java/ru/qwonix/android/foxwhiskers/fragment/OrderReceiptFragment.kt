package ru.qwonix.android.foxwhiskers.fragment

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentOrderReceiptBinding
import ru.qwonix.android.foxwhiskers.entity.Order
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.fragment.adapter.OrderReceiptAdapter
import ru.qwonix.android.foxwhiskers.fragment.adapter.PickUpLocationClickListener
import ru.qwonix.android.foxwhiskers.fragment.adapter.QrCodeClickListener
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler
import ru.qwonix.android.foxwhiskers.viewmodel.OrderViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.ProfileViewModel


@AndroidEntryPoint
class OrderReceiptFragment : Fragment(R.layout.fragment_order_receipt) {

    private val TAG = "OrderReceiptFragment"

    private lateinit var binding: FragmentOrderReceiptBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderReceiptBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderReceiptAdapter = OrderReceiptAdapter()
        orderReceiptAdapter.qrCodeClickListener =
            object : QrCodeClickListener {
                override fun onQrClick(order: Order) {
                    QrBottomSheetDialogFragment(order.id).show(
                        parentFragmentManager,
                        "tag"
                    )
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
                    orderReceiptAdapter.setOrders(it.data)
                }
            }
        }

        orderViewModel.loadOrders((profileViewModel.clientAuthenticationResponse.value as ApiResponse.Success).data!!.phoneNumber,
            object : CoroutinesErrorHandler {
                override fun onError(message: String) {
                    TODO("Not yet implemented")
                }
            })
    }
}