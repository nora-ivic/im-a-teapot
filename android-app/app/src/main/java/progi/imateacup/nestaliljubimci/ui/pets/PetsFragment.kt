package progi.imateacup.nestaliljubimci.ui.pets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.FragmentPetsBinding
import progi.imateacup.nestaliljubimci.networking.ApiModule
import progi.imateacup.nestaliljubimci.ui.authentication.LoginFragment.Companion.ACCESS_TOKEN
import progi.imateacup.nestaliljubimci.ui.authentication.PREFERENCES_NAME
import progi.imateacup.nestaliljubimci.util.isInternetAvailable

class PetsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: PetsAdapter

    private var accessToken: String? = null
    private var _binding: FragmentPetsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PetsViewModel>()
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
        if(accessToken != null) {
            binding.topAppBar.navigationIcon = null
        }
        initRecyclerViewAdapter()
        setLiveDataObservers()
        viewModel.getPets(isInternetAvailable(requireContext()))
        initListeners()
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
            val layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        if (isInternetAvailable(requireContext())) {
                            viewModel.getPets(isInternetAvailable(requireContext()))
                        }
                    }
                }
            })
        }
    }
    private fun initRecyclerViewAdapter() {
        adapter = PetsAdapter(emptyList()) {
            /*val direction = PetsFragmentDirections.actionPetsFragmentToPetDetailsFragment()
            findNavController().navigate(direction)*/
        }
        binding.recyclerView.adapter = adapter
    }

    private fun setLiveDataObservers() {
        viewModel.petsLiveData.observe(viewLifecycleOwner) { pets ->
            if (pets != null && pets != adapter.getPetsList()) {
                adapter.updateData(pets)
            }
        }
        viewModel.getPetsSuccessLiveData.observe(viewLifecycleOwner) { success ->
            if (!success) {
                Snackbar.make(binding.recyclerView, getString(R.string.get_pets_failed), Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
