package progi.imateacup.nestaliljubimci.ui.map

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private lateinit var _binding: FragmentMapBinding

    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleMapClick()
    }

    private fun handleMapClick() {
        val navController = findNavController()
        val map = binding.map.getMapboxMap()
        var latitude = 0.0
        var longitude = 0.0
        map.addOnMapClickListener { point ->
            latitude = point.latitude()
            longitude = point.longitude()
            navController.previousBackStackEntry?.savedStateHandle?.set(
                "coordinates",
                "$latitude,$longitude"
            )
            navController.popBackStack()
            true
        }
    }
}