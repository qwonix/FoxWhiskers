package ru.qwonix.android.foxwhiskers.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.qwonix.android.foxwhiskers.DishAdapter
import ru.qwonix.android.foxwhiskers.DishType
import ru.qwonix.android.foxwhiskers.DishTypeChipAdapter
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentMenuBinding
import ru.qwonix.android.foxwhiskers.entity.Dish

class MenuFragment : Fragment(R.layout.fragment_menu) {
    companion object {
        /**
         * Load picture from the internet via Picasso
         * @see Picasso
         */
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, imageUrl: String?) {
            if (imageUrl != null) {
                if (imageUrl.isNotBlank()) {
                    println("test")
                    Picasso.get()
                        .load(imageUrl)
//                        .placeholder(R.drawable.placeholder)
//                        .error(R.drawable.error)
                        .into(view)
                }
            } else {
//                view.setImageDrawable(R.drawable.placeholder)
            }
        }
    }

    private lateinit var binding: FragmentMenuBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dishAdapter = DishAdapter()
        dishAdapter.dishes = listOf(
            Dish("Пицца", "https://i.imgur.com/dNpAg7f.jpg", "целая, 42 см, 1350 гр", "2131.32 ₽", "Пицца"),
            Dish("Пицца", "https://i.imgur.com/H1ieAcE.png", "целая, 42 см, 1350 гр", "123 ₽", "Пицца"),
            Dish("Пицца", "https://i.imgur.com/H1ieAcE.png", "целая, 42 см, 1350 гр", "669 ₽", "Пицца"),
            Dish("Пицца", "https://i.imgur.com/kzUwGbe.jpg", "целая, 42 см, 1350 гр", "842 ₽", "Пицца")
        )
        val dishTypeAdapter = DishTypeChipAdapter()
        dishTypeAdapter.dishTypes = listOf(
            DishType("Пицца"),
            DishType("суп"),
            DishType("каша"),
            DishType("морс"),
        )
//        sharedSearchSettingsViewModel.apartments.observe(viewLifecycleOwner) {
//            apartmentsAdapter.apartments = it
//        }

        binding.recyclerDishTypeChip.apply {
            adapter = dishTypeAdapter
            val manager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            layoutManager = manager

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.right = 15
                }
            })
        }

        binding.recyclerDishes.apply {
            adapter = dishAdapter
            val manager = GridLayoutManager(context, 2)
            layoutManager = manager

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = 30
                    outRect.right = 30
                }
            })
        }
    }
}