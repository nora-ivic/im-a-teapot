package progi.imateacup.nestaliljubimci.ui.pets

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.json.Json
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.FilterInputDialogBinding
import progi.imateacup.nestaliljubimci.databinding.FragmentPetsBinding
import progi.imateacup.nestaliljubimci.model.networking.entities.SearchFilter
import progi.imateacup.nestaliljubimci.model.networking.enums.AppState
import progi.imateacup.nestaliljubimci.ui.authentication.LoginFragment.Companion.ACCESS_TOKEN
import progi.imateacup.nestaliljubimci.ui.authentication.PREFERENCES_NAME
import progi.imateacup.nestaliljubimci.util.isInternetAvailable

class PetsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: PetsAdapter
    private lateinit var usernameDialog: AlertDialog
    private lateinit var shelterDialog: AlertDialog
    private var filter: SearchFilter? = null
    private var filterPresent = false

    private var accessToken: String? = null
    private var _binding: FragmentPetsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PetsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        buildUsernameInputDialog()
        buildShelterNameDialog()
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
        if (accessToken != null) {
            binding.topAppBar.navigationIcon = null
        }
        initRecyclerViewAdapter()
        setLiveDataObservers()
        if (!filterPresent) {
            viewModel.getPets(isInternetAvailable(requireContext()), SearchFilter())
        }
        initListeners()
        MenuCompat.setGroupDividerEnabled(binding.topAppBar.menu, true)
    }

    private fun initListeners() {
        with(binding) {
            topAppBar.setNavigationOnClickListener {
                val direction = PetsFragmentDirections.actionPetsFragmentToLoginFragment()
                findNavController().navigate(direction)
            }
            handleMenu()

            ocistiFilter.setOnClickListener {
                filterPresent = false
                filter = SearchFilter()
                currentFilter.text = ""
                filterDisplay.visibility = View.GONE
                viewModel.getPets(isInternetAvailable(requireContext()), filter!!)
            }

            tryAgain.setOnClickListener {
                viewModel.getPets(isInternetAvailable(requireContext()), filter ?: SearchFilter())
            }

            bottomAppBar.menu.findItem(R.id.mojiOglasi).setOnMenuItemClickListener {
                /** TODO
                 * Dohvati moje ljubimce
                 */
                filterDisplay.visibility = View.VISIBLE
                currentFilter.text = getString(R.string.mojiOglasi)
                true
            }
            dodajOglas.setOnClickListener {
                /*val direction = PetsFragmentDirections.actionPetsFragmentToHandlePostFragment()
                findNavController().navigate(direction)*/
            }
            val layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    Log.d("PetsFragment", "Scroll listener triggered")
                    super.onScrolled(recyclerView, dx, dy)

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        if (isInternetAvailable(requireContext())) {
                            viewModel.getPets(
                                isInternetAvailable(requireContext()),
                                filter ?: SearchFilter()
                            )
                        }
                    }
                }
            })
        }
    }

    private fun handleMenu() {
        with(binding) {
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.izgubljeni -> {
                        filterAndGetForCategory("Izgubljeni")
                        currentFilter.text = getString(R.string.izgubljeni)
                        true
                    }

                    R.id.pronadeni -> {
                        filterAndGetForCategory("Pronadeni")
                        currentFilter.text = getString(R.string.pronadeni)
                        true
                    }

                    R.id.prekinutoTrazenje -> {
                        filterAndGetForCategory("PrekinutoTrazenje")
                        currentFilter.text = getString(R.string.prekinutoTrazenje)
                        true
                    }

                    R.id.uSklonistu -> {
                        filterAndGetForCategory("USklonistu")
                        currentFilter.text = getString(R.string.uSklonistu)
                        true
                    }

                    R.id.po_ljubimcu -> {
                        val direction = PetsFragmentDirections.actionPetsFragmentToSearchFragment()
                        filterPresent = true
                        findNavController().navigate(direction)
                        true
                    }

                    R.id.trazi_po_korisniku -> {
                        usernameDialog.show()
                        filterPresent = true
                        true
                    }

                    R.id.trazi_po_sklonistu -> {
                        shelterDialog.show()
                        filterPresent = true
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun filterAndGetForCategory(category: String) {
        filter = SearchFilter(kategorijaOglasa = category)
        viewModel.getPets(isInternetAvailable(requireContext()), filter!!)
        filterPresent = true
        binding.filterDisplay.visibility = View.VISIBLE
    }

    private fun initRecyclerViewAdapter() {
        adapter = PetsAdapter(emptyList()) { post ->
            val direction =
                PetsFragmentDirections.actionPetsFragmentToDetailedViewFragment(advertId = post.postId)
            findNavController().navigate(direction)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun setLiveDataObservers() {
        viewModel.petsLiveData.observe(viewLifecycleOwner) { pets ->
            if (!pets.isNullOrEmpty() && pets != adapter.getPetsList()) {
                adapter.updateData(pets)
            }
        }
        viewModel.appStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                AppState.LOADING -> {
                    showLoading()
                }

                AppState.SUCCESS -> {
                    showPets()
                }

                AppState.ERROR -> {
                    showNoPosts()
                    Snackbar.make(
                        binding.root,
                        "Došlo je do pogreške prilikom dohvaćanja oglasa",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("filter")
            ?.observe(
                viewLifecycleOwner
            ) { filterJson ->
                Log.d("PetsFragment", "Filter: $filterJson")
                filter = Json.decodeFromString(filterJson)
                viewModel.getPets(isInternetAvailable(requireContext()), filter!!)
                binding.filterDisplay.visibility = View.VISIBLE
            }
    }

    private fun buildUsernameInputDialog() {
        val userDialogBinding = FilterInputDialogBinding.inflate(layoutInflater)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        usernameDialog = builder.setPositiveButton("Pretraži") { dialog, _ ->
            val username = userDialogBinding.textInputEditText.text.toString()
            if (username.isNotEmpty()) {
                filter = SearchFilter(korisnickoIme = username)
                binding.currentFilter.text = username
                binding.filterDisplay.visibility = View.VISIBLE
                viewModel.getPets(isInternetAvailable(requireContext()), filter!!)
                dialog.dismiss()
            }
        }.setNegativeButton("Odustani") { dialog, _ ->
            dialog.dismiss()
        }.setView(R.layout.filter_input_dialog).setTitle("Unesite traženo korisničko ime").create()

        userDialogBinding.outlineTextFieldLayout.hint = "Korisničko ime"
        usernameDialog.setView(userDialogBinding.root)
    }

    private fun buildShelterNameDialog() {
        val shelterDialogBinding = FilterInputDialogBinding.inflate(layoutInflater)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        shelterDialog = builder.setPositiveButton("Pretraži") { dialog, _ ->
            val shelterName = shelterDialogBinding.textInputEditText.text.toString()
            if (shelterName.isNotEmpty()) {
                filter = SearchFilter(imeSklonista = shelterName)
                binding.currentFilter.text = shelterName
                binding.filterDisplay.visibility = View.VISIBLE
                viewModel.getPets(isInternetAvailable(requireContext()), filter!!)
                dialog.dismiss()
            }
        }.setNegativeButton("Odustani") { dialog, _ ->
            dialog.dismiss()
        }.setView(R.layout.filter_input_dialog).setTitle("Unesite traženo ime skloništa").create()

        shelterDialogBinding.outlineTextFieldLayout.hint = "Ime skloništa"
        shelterDialog.setView(shelterDialogBinding.root)
    }

    private fun showPets() {
        with(binding) {
            recyclerView.visibility = View.VISIBLE
            postsLoadingProgressBar.visibility = View.GONE
            noPostsDisplay.visibility = View.GONE
        }
    }

    private fun showLoading() {
        with(binding) {
            recyclerView.visibility = View.GONE
            postsLoadingProgressBar.visibility = View.VISIBLE
            noPostsDisplay.visibility = View.GONE
        }
    }

    private fun showNoPosts() {
        with(binding) {
            recyclerView.visibility = View.GONE
            postsLoadingProgressBar.visibility = View.GONE
            noPostsDisplay.visibility = View.VISIBLE
        }
    }
}