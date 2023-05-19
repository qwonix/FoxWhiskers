package ru.qwonix.android.foxwhiskers.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.databinding.ItemOrderReceiptBinding
import ru.qwonix.android.foxwhiskers.entity.Order
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.util.Utils


class OrderReceiptAdapter : RecyclerView.Adapter<OrderReceiptAdapter.ViewHolder>() {
    private val data = mutableListOf<Order>()

    fun setOrders(orders: List<Order>) {
        data.clear()
        data.addAll(orders)
        notifyDataSetChanged()
    }

    interface OnQrCodeClickListener {
        fun onQrClick(order: Order)
    }

    lateinit var onQrCodeClickListener: OnQrCodeClickListener

    interface OnPickUpLocationClickListener {
        fun onPickUpLocationClick(pickUpLocation: PickUpLocation)
    }

    lateinit var onPickUpLocationClickListener: OnPickUpLocationClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemOrderReceiptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(
        private val binding: ItemOrderReceiptBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.order = order
            binding.priceFormat = Utils.DECIMAL_FORMAT
            binding.openQrCodeButton.setOnClickListener {
                onQrCodeClickListener.onQrClick(order)
            }
            binding.pickUpLocationButton.setOnClickListener {
                onPickUpLocationClickListener.onPickUpLocationClick(order.pickUpLocation)
            }
        }
    }
}