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
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.ui_view.ViewProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentPickUpLocationBinding
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.util.ForEachMapObjectVisitor
import ru.qwonix.android.foxwhiskers.util.FoxWhiskersSnackBar
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

    private val bottomCenter = IconStyle().apply { anchor = PointF(0.5f, 1.0f) }

    private lateinit var unselectedPoint: ViewProvider
    private lateinit var selectedPoint: ViewProvider

    private lateinit var binding: FragmentPickUpLocationBinding

    private val changePlacemarkViewTapListener = MapObjectTapListener { tappedMapObject, _ ->
        tappedMapObject as PlacemarkMapObject

        // if current selected
        if (binding.pickupLocation == tappedMapObject.userData) {
            return@MapObjectTapListener true
        }

        // set previous selected as default
        setViewToMapObject(binding.pickupLocation!!, unselectedPoint, bottomCenter)

        // set tapped object as selected
        tappedMapObject.setView(selectedPoint, bottomCenter)

        pickUpLocationViewModel.setSelectedPickUpLocation(
            tappedMapObject.userData as PickUpLocation,
            object : CoroutinesErrorHandler {
                override fun onError(message: String) {
                    TODO("Not yet implemented")
                }
            }
        )

        true
    }

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
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // move camera to init position
        binding.mapview.map.move(
            CameraPosition(
                Utils.mapInitPosition, 10.0f, 0.0f, 0.0f
            )
        )

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
                    it.data.forEach { location ->
                        if (binding.pickupLocation != location) {
                            addLocationToMap(
                                location,
                                unselectedPoint,
                                bottomCenter,
                                changePlacemarkViewTapListener
                            )
                        } else {
                            addLocationToMap(
                                location,
                                selectedPoint,
                                bottomCenter,
                                changePlacemarkViewTapListener
                            )
                        }
                    }
                }
            }
        }

        pickUpLocationViewModel.selectedPickUpLocationResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")

                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful load orders ${it.data}")
                    Log.i(TAG, "set $it as selected")
                    binding.apply {
                        // smooth move camera to current selected
                        mapview.map.move(
                            CameraPosition(
                                Point(
                                    it.data!!.latitude,
                                    it.data.longitude
                                ), 14.0f, 0.0f, 10.0f
                            ), Animation(Animation.Type.SMOOTH, 1f)
                        ) { }
                        // set content of current selected
                        pickupLocation = it.data

                        addLocationToMap(
                            it.data,
                            selectedPoint,
                            bottomCenter,
                            changePlacemarkViewTapListener
                        )

                        // set icon for current selected
                        setViewToMapObject(it.data, selectedPoint, bottomCenter)
                    }
                }
            }
        }

        // onclick open a point in the map application
        binding.buildRouteButton.setOnClickListener {
            when (val apiResponse =
                pickUpLocationViewModel.selectedPickUpLocationResponse.value) {
                is ApiResponse.Failure -> {
                    Log.e(
                        TAG,
                        "fail to load profile code: ${apiResponse.code} – ${apiResponse.errorMessage}"
                    )
                    FoxWhiskersSnackBar.make(
                        view,
                        "Не удалось открыть приложение для карт :( ",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")

                is ApiResponse.Success -> {
                    val mapIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            "geo:${apiResponse.data!!.latitude}," +
                                    "${apiResponse.data.longitude}?q=Усы+Лисы&z=15"
                        )
                    )
                    // suggest selection of an application
                    this.startActivity(
                        Intent.createChooser(
                            mapIntent,
                            "Где построить маршрут в Усы Лисы?"
                        )
                    )
                }

                null -> TODO()
            }
        }

        pickUpLocationViewModel.tryLoadPickUpLocations(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                FoxWhiskersSnackBar.make(
                    view,
                    "Нет подключения к интернету :( ",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

        pickUpLocationViewModel.tryLoadSelectedPickUpLocation(object :
            CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun addLocationToMap(
        pickUpLocation: PickUpLocation,
        viewProvider: ViewProvider,
        iconStyle: IconStyle,
        tapListener: MapObjectTapListener
    ) {
        binding.mapview.map.mapObjects.addPlacemark(
            Point(pickUpLocation.latitude, pickUpLocation.longitude), viewProvider, iconStyle
        ).apply {
            userData = pickUpLocation
            addTapListener(tapListener)
        }
    }

    private fun setViewToMapObject(
        mapObjectUserData: Any,
        viewProvider: ViewProvider,
        iconStyle: IconStyle
    ) {
        val forEachMapObjectVisitor =
            object : ForEachMapObjectVisitor() {
                override fun onPlacemarkVisited(placemarkMapObject: PlacemarkMapObject) {
                    if (placemarkMapObject.userData == mapObjectUserData) {
                        placemarkMapObject.setView(viewProvider, iconStyle)
                    }
                }
            }

        binding.mapview.map.mapObjects.traverse(forEachMapObjectVisitor)
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
}