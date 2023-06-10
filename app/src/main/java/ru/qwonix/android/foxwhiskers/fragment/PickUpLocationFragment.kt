package ru.qwonix.android.foxwhiskers.fragment

import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.MapObjectVisitor
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.runtime.ui_view.ViewProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentPickUpLocationBinding
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.util.Utils
import ru.qwonix.android.foxwhiskers.util.withDemoBottomSheet
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler
import ru.qwonix.android.foxwhiskers.viewmodel.PickUpLocationViewModel


@AndroidEntryPoint
class PickUpLocationFragment : Fragment(R.layout.fragment_pick_up_location) {

    private val TAG = "OderPickUpLocationFrag"

    companion object {
        fun newInstance() = PickUpLocationFragment()
    }

    private val pickUpLocationViewModel: PickUpLocationViewModel by activityViewModels()

    private val iconStyle = IconStyle().apply { anchor = PointF(0.5f, 1.0f) }

    private lateinit var unselectedPoint: ViewProvider
    private lateinit var selectedPoint: ViewProvider

    private lateinit var binding: FragmentPickUpLocationBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

        unselectedPoint = ViewProvider(View(context).apply {
            background = AppCompatResources.getDrawable(context, R.drawable.ic_unselected_map_point)
        })

        selectedPoint = ViewProvider(View(context).apply {
            background = AppCompatResources.getDrawable(context, R.drawable.ic_selected_map_point)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentPickUpLocationBinding.inflate(inflater, container, false)

        binding.apply {
            // FIXME: pass directly menuViewModel
            pickupLocation = pickUpLocationViewModel.selectedPickUpLocation.value
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
        binding.buildRouteButton.setOnClickListener {
            val mapIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    "geo:${pickUpLocationViewModel.selectedPickUpLocation.value?.latitude ?: Utils.primaryCityPoint.latitude}," +
                            "${pickUpLocationViewModel.selectedPickUpLocation.value?.longitude ?: Utils.primaryCityPoint.longitude}?q=Усы+Лисы&z=15"
                )
            )
            // suggest selection of an application
            this.startActivity(Intent.createChooser(mapIntent, "Где построить маршрут?"))
        }

        binding.selectLocationButton.setOnClickListener {
            withDemoBottomSheet { goBack() }
        }

        binding.goBackButton.setOnClickListener {
            withDemoBottomSheet { goBack() }
        }

        pickUpLocationViewModel.pickUpLocations.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")

                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful load locations ${it.data}")
                    binding.mapview.map.mapObjects.clear()
                    addLocationsToMap(it.data)

                    pickUpLocationViewModel.selectedPickUpLocation.observe(viewLifecycleOwner) {
                        Log.i(TAG, "set $it as selected")
                        binding.apply {
                            // smooth move camera to current selected
                            mapview.map.move(
                                CameraPosition(
                                    Point(
                                        it.latitude,
                                        it.longitude
                                    ), 14.0f, 0.0f, 10.0f
                                ), Animation(Animation.Type.SMOOTH, 1f)
                            ) { }
                            // set content of current selected
                            pickupLocation = it
                            // set icon for current selected
                            setViewByUserData(it, selectedPoint)
                        }
                    }
                }
            }
        }
    }

    private fun addLocationsToMap(pickUpLocations: List<PickUpLocation>) {
        pickUpLocations.forEach { addLocationToMap(it) }
    }


    private fun addLocationToMap(pickUpLocation: PickUpLocation) {
        binding.mapview.map.mapObjects.addPlacemark(
            Point(pickUpLocation.latitude, pickUpLocation.longitude), unselectedPoint
        ).apply {
            userData = pickUpLocation
            addTapListener(changePlacemarkViewTapListener)
        }
    }


    private val changePlacemarkViewTapListener = MapObjectTapListener { tappedMapObject, _ ->
        tappedMapObject as PlacemarkMapObject

        // if current selected
        if (pickUpLocationViewModel.selectedPickUpLocation.value == tappedMapObject.userData) {
            return@MapObjectTapListener true
        }

        // set previous selected as default
        setViewByUserData(pickUpLocationViewModel.selectedPickUpLocation.value!!, unselectedPoint)

        // set tapped object as selected
        tappedMapObject.setView(selectedPoint, iconStyle)
        pickUpLocationViewModel.setSelectedPickUpLocation(
            object : CoroutinesErrorHandler {
                override fun onError(message: String) {
                    TODO("Not yet implemented")
                }
            }
            , tappedMapObject.userData as PickUpLocation)

        true
    }

    private fun setViewByUserData(userData: Any, viewProvider: ViewProvider) {
        val changeViewByUserDataVisitor = ChangeViewByUserDataVisitor(userData, viewProvider)
        binding.mapview.map.mapObjects.traverse(changeViewByUserDataVisitor)
    }

    override fun onStart() {
        super.onStart()
        binding.mapview.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }


    inner class ChangeViewByUserDataVisitor(
        private val userData: Any,
        private val viewProvider: ViewProvider
    ) : MapObjectVisitor {
        override fun onPlacemarkVisited(placemarkMapObject: PlacemarkMapObject) {
            if (placemarkMapObject.userData == this.userData) {
                placemarkMapObject.setView(this.viewProvider, iconStyle)
            }
        }

        override fun onPolylineVisited(p0: PolylineMapObject) {}

        override fun onPolygonVisited(p0: PolygonMapObject) {}

        override fun onCircleVisited(p0: CircleMapObject) {}

        override fun onCollectionVisitStart(p0: MapObjectCollection): Boolean {
            return true
        }

        override fun onCollectionVisitEnd(p0: MapObjectCollection) {}

        override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection): Boolean {
            return true
        }

        override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {}
    }
}