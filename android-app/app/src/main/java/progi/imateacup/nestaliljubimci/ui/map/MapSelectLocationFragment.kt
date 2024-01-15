package progi.imateacup.nestaliljubimci.ui.map
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import progi.imateacup.nestaliljubimci.databinding.FragmentSelectMapBinding
import progi.imateacup.nestaliljubimci.ui.authentication.PREFERENCES_NAME

class MapSelectLocationFragment : Fragment() {

    private lateinit var _binding: FragmentSelectMapBinding
    private val RETURNED_FROM_MAP_FRAGMENT_KEY = "returned_from_map_fragment"
    private var returnedFromMap: Boolean = false

    private lateinit var sharedPreferences: SharedPreferences

    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectMapBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        returnedFromMap = true
        val editor = sharedPreferences.edit()
        editor.putBoolean(RETURNED_FROM_MAP_FRAGMENT_KEY, returnedFromMap)
        editor.apply()

        handleMapClick()
    }

    private fun handleMapClick() {
        val navController = findNavController()
        val map = binding.mapSelect.getMapboxMap()
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