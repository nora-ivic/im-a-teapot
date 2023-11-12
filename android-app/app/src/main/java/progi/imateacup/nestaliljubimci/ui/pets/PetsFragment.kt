package progi.imateacup.nestaliljubimci.ui.pets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.FragmentPetsBinding
import progi.imateacup.nestaliljubimci.ui.authentication.LoginFragment.Companion.ACCESS_TOKEN
import progi.imateacup.nestaliljubimci.ui.authentication.PREFERENCES_NAME

class PetsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private var accessToken: String? = null
    private var _binding: FragmentPetsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        if(accessToken != null) {
            binding.topAppBar.navigationIcon = null
        }
    }

    private fun initListeners() {
        with(binding) {
            topAppBar.setNavigationOnClickListener {
                val direction = PetsFragmentDirections.actionPetsFragmentToLoginFragment()
                findNavController().navigate(direction)
            }
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.search -> {
                        /*val direction = PetsFragmentDirections.actionPetsFragmentTo<ime_search_fragmenta>Fragment()
                        findNavController().navigate(direction)*/
                        true
                    }
                    else -> false
                }
            }
            bottomAppBar.setNavigationOnClickListener {
                /*val direction = PetsFragmentDirections.actionPetsFragmentToMyPetsFragment()
                findNavController().navigate(direction)*/
            }
            dodajOglas.setOnClickListener {
                /*val direction = PetsFragmentDirections.actionPetsFragmentToHandlePostFragment()
                findNavController().navigate(direction)*/
            }
        }
    }
}