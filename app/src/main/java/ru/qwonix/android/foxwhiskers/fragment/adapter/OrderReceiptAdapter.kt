package ru.qwonix.android.foxwhiskers.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.databinding.ItemOrderReceiptBinding
import ru.qwonix.android.foxwhiskers.entity.Order
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.util.Utils


interface QrCodeClickListener {
    fun onQrClick(order: Order)
}

interface PickUpLocationClickListener {
    fun onPickUpLocationClick(pickUpLocation: PickUpLocation)
}

class OrderReceiptAdapter : RecyclerView.Adapter<OrderReceiptAdapter.ViewHolder>() {

    lateinit var qrCodeClickListener: QrCodeClickListener
    lateinit var pickUpLocationClickListener: PickUpLocationClickListener

    private val data = mutableListOf<Order>()

    fun setOrders(orders: List<Order>) {
        data.clear()
        data.addAll(orders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemOrderReceiptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], qrCodeClickListener, pickUpLocationClickListener)
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(
        private val binding: ItemOrderReceiptBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            order: Order,
            qrCodeClickListener: QrCodeClickListener,
            pickUpLocationClickListener: PickUpLocationClickListener
        ) {
            binding.order = order
//            binding.orderStatusTextView.setBackgroundResource(order.status.colorId)
            binding.priceFormat = Utils.DECIMAL_FORMAT
            binding.qrCodeClickListener = qrCodeClickListener
            binding.pickUpLocationClickListener = pickUpLocationClickListener
        }
    }
}