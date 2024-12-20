package ru.vreztsov.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import ru.vreztsov.nework.R
import ru.vreztsov.nework.databinding.FragmentMapBinding
import ru.vreztsov.nework.viewmodel.PostViewModel

class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private lateinit var map: Map
    private var zoomMap: Float = 2.0f
    val postViewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(layoutInflater)
        map = binding.mapview.mapWindow.map
        postViewModel.withCoords { latitude, longitude ->
            val latitudeMap = latitude ?: 0.0
            val longitudeMap = longitude ?: 0.0
            map.move(
                CameraPosition(Point(latitudeMap, longitudeMap), zoomMap, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 2F),
                null
            )
        }
        zoomMap = map.cameraPosition.zoom
        setListeners()
        return binding.root
    }

    private fun setListeners() {
        with(binding) {
            zoomIn.setOnClickListener {
                zoom(2F)
                zoomMap = map.cameraPosition.zoom
            }
            zoomOut.setOnClickListener {
                zoom(-2F)
                zoomMap = map.cameraPosition.zoom
            }
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            map.addCameraListener { _, cameraPosition, _, _ ->
                topAppBar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.save -> {
                            val point = cameraPosition.target
                            postViewModel.setCoords(
                                point.latitude,
                                point.longitude
                            )
                            findNavController().navigateUp()
                            true
                        }

                        else -> false
                    }
                }
            }
        }
    }

    private fun zoom(delta: Float) {
        with(map.cameraPosition) {
            map.move(
                CameraPosition(target, zoom + delta, azimuth, tilt),
                Animation(Animation.Type.SMOOTH, 0.5F),
                null
            )
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}