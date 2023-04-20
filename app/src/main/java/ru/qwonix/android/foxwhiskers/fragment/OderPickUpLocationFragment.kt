package ru.qwonix.android.foxwhiskers.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.ui_view.ViewProvider
import ru.qwonix.android.foxwhiskers.BR
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentOrderPickupLocationBinding
import ru.qwonix.android.foxwhiskers.entity.Location
import ru.qwonix.android.foxwhiskers.util.Utils
import ru.qwonix.android.foxwhiskers.viewmodel.MenuViewModel


class OderPickUpLocationFragment : Fragment(R.layout.fragment_order_pickup_location) {
    companion object {
        fun newInstance() = OderPickUpLocationFragment()
    }

    private val menuViewModel: MenuViewModel by activityViewModels()

    private lateinit var defaultPoint: ViewProvider
    private lateinit var selectedPoint: ViewProvider

    private val changeSelectedLocationTapListener = MapObjectTapListener { mapObject, _ ->
        this@OderPickUpLocationFragment.selectedLocation = mapObject.userData as Location
        true
    }

    private val changePlacemarkViewTapListener = MapObjectTapListener { mapObject, _ ->
        mapObject as PlacemarkMapObject
        if (this@OderPickUpLocationFragment.selectedLocation != mapObject.userData as Location) {
            mapObject.setView(this@OderPickUpLocationFragment.defaultPoint)
            mapObject.setView(this@OderPickUpLocationFragment.selectedPoint)

        } else {
            mapObject.setView(this@OderPickUpLocationFragment.defaultPoint)
        }
        true
    }

    private lateinit var binding: FragmentOrderPickupLocationBinding

    private lateinit var locations: List<Location>

    private var selectedLocation: Location = menuViewModel.locations.random()
        set(value) {
            field = value
            binding.location = value
            binding.notifyPropertyChanged(BR.location)
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        defaultPoint = ViewProvider(View(context).apply {
            background = AppCompatResources.getDrawable(context, R.drawable.ic_point)
        })

        selectedPoint = ViewProvider(View(context).apply {
            background = AppCompatResources.getDrawable(context, R.drawable.ic_map_point)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentOrderPickupLocationBinding.inflate(inflater, container, false)

        binding.apply {
            location = this@OderPickUpLocationFragment.selectedLocation
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // move camera to init position
        binding.mapview.map.move(
            CameraPosition(
                Utils.primaryCityPoint, 10.0f, 0.0f, 0.0f
            )
        )

        // onclick open a point in the map application
        binding.planRoute.setOnClickListener {
            val mapIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:${selectedLocation.latitude},${selectedLocation.longitude}?q=Усы+Лисы&z=15")
            )
            // suggest selection of an application
            this.startActivity(Intent.createChooser(mapIntent, "Где построить маршрут?"))
        }

        // smooth move camera to cur
        binding.mapview.map.move(
            CameraPosition(
                Point(selectedLocation.latitude, selectedLocation.longitude), 14.0f, 0.0f, 10.0f
            ), Animation(Animation.Type.SMOOTH, 2f)
        ) { }

        addLocationsToMap(locations)
    }


    private fun addLocationsToMap(location: Location) {
        binding.mapview.map.mapObjects.addPlacemark(
            Point(location.latitude, location.longitude), defaultPoint
        ).apply {
            userData = location
            addTapListener(changeSelectedLocationTapListener)
            addTapListener(changePlacemarkViewTapListener)
        }
    }


    private fun addLocationsToMap(locations: List<Location>) {
        locations.forEach { addLocationsToMap(it) }
    }

    override fun onStop() {
        super.onStop()
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        binding.mapview.onStart()
        MapKitFactory.getInstance().onStart()
    }
}