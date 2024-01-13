package progi.imateacup.nestaliljubimci.ui.pets

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.json.Json
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.FilterInputDialogBinding
import progi.imateacup.nestaliljubimci.databinding.DialogProfileBinding
import progi.imateacup.nestaliljubimci.databinding.FragmentPetsBinding
import progi.imateacup.nestaliljubimci.model.networking.entities.SearchFilter
import progi.imateacup.nestaliljubimci.model.networking.enums.AdvertisementCategory
import progi.imateacup.nestaliljubimci.model.networking.enums.PetsDisplayState
import progi.imateacup.nestaliljubimci.model.networking.response.Pet
import progi.imateacup.nestaliljubimci.ui.authentication.LoginFragment.Companion.ACCESS_TOKEN
import progi.imateacup.nestaliljubimci.ui.authentication.PREFERENCES_NAME
import progi.imateacup.nestaliljubimci.util.isInternetAvailable

class PetsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: PetsAdapter
    private lateinit var usernameDialog: AlertDialog
    private lateinit var shelterDialog: AlertDialog
    private var filter: SearchFilter? = null

    private var accessToken: String? = null
    private var _binding: FragmentPetsBinding? = null

    private val args by navArgs<PetsFragmentArgs>()

    private val binding get() = _binding!!

    private val viewModel by viewModels<PetsViewModel>()
    private var profileDialogClosed = true
    private var displayMyPets = false

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
            binding.topAppBarPets.navigationIcon = null
        }
        initRecyclerViewAdapter()
        setLiveDataObservers()
        initListeners()
        MenuCompat.setGroupDividerEnabled(binding.topAppBarPets.menu, true)
        binding.currentFilter.text = sharedPreferences.getString("lastFilterTitle", "")
    }

    private fun initListeners() {
        with(binding) {
            if (accessToken != null) {
                topAppBarPets.setNavigationIcon(R.drawable.icons_user_big)
                topAppBarPets.setNavigationOnClickListener {
                    val dialog = buildUserInfoDialog()
                    if (profileDialogClosed) {
                        profileDialogClosed = false
                        dialog.show()
                    }
                }
            } else {
                topAppBarPets.setNavigationOnClickListener {
                    val direction = PetsFragmentDirections.actionPetsFragmentToLoginFragment()
                    findNavController().navigate(direction)
                }
            }
            handleMenu()

            ocistiFilter.setOnClickListener {
                getPets(null, false, "", true)
            }

            tryAgain.setOnClickListener {
                getPets(filter, displayMyPets, null)
            }

            bottomAppBar.menu.findItem(R.id.mojiOglasi).setOnMenuItemClickListener {
                getPets(null, true, getString(R.string.mojiOglasi))
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
                    super.onScrolled(recyclerView, dx, dy)

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        if (isInternetAvailable(requireContext())) {
                            getPets(
                                filter,
                                displayMyPets,
                                null
                            )
                        }
                    }
                }
            })
        }
    }

    private fun buildUserInfoDialog(): BottomSheetDialog {
        val dialog = BottomSheetDialog(requireContext())
        val dialogProfileBinding = DialogProfileBinding.inflate(layoutInflater)
        dialog.setContentView(dialogProfileBinding.root)

        dialog.setOnDismissListener {
            profileDialogClosed = true
        }

        with(dialogProfileBinding) {
            textViewEmail.text = args.email
            textViewUsername.text = args.username
            textViewPhoneNumber.text = args.phoneNumber
        }

        return dialog
    }


    private fun handleMenu() {
        with(binding) {
            if (accessToken == null) {
                topAppBarPets.menu.removeItem(R.id.pronadeni)
                topAppBarPets.menu.removeItem(R.id.trazi_po_sklonistu)
                topAppBarPets.menu.removeItem(R.id.prekinutoTrazenje)
                topAppBarPets.menu.removeItem(R.id.uSklonistu)
                topAppBarPets.menu.removeItem(R.id.uginuli)
            }

            topAppBarPets.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.izgubljeni -> {
                        getPets(
                            SearchFilter(kategorijaOglasa = AdvertisementCategory.lost.toString()),
                            false,
                            getString(R.string.izgubljeni)
                        )
                        true
                    }

                    R.id.pronadeni -> {
                        getPets(
                            SearchFilter(kategorijaOglasa = AdvertisementCategory.found.toString()),
                            false,
                            getString(R.string.pronadeni)
                        )
                        true
                    }

                    R.id.prekinutoTrazenje -> {
                        getPets(
                            SearchFilter(kategorijaOglasa = AdvertisementCategory.abandoned.toString()),
                            false,
                            getString(R.string.prekinutoTrazenje)
                        )
                        true
                    }

                    R.id.uSklonistu -> {
                        getPets(
                            SearchFilter(kategorijaOglasa = AdvertisementCategory.sheltered.toString()),
                            false,
                            getString(R.string.uSklonistu)
                        )
                        true
                    }

                    R.id.uginuli -> {
                        getPets(
                            SearchFilter(kategorijaOglasa = AdvertisementCategory.dead.toString()),
                            false,
                            getString(R.string.uginuli)
                        )
                        true
                    }

                    R.id.po_ljubimcu -> {
                        val direction = PetsFragmentDirections.actionPetsFragmentToSearchFragment()
                        findNavController().navigate(direction)
                        true
                    }

                    R.id.trazi_po_korisniku -> {
                        usernameDialog.show()
                        true
                    }

                    R.id.trazi_po_sklonistu -> {
                        shelterDialog.show()
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun initRecyclerViewAdapter() {
        adapter = PetsAdapter(emptyList(),
            onPetPostClickCallback =
            { advert ->
                val direction =
                    PetsFragmentDirections.actionPetsFragmentToDetailedViewFragment(advertId = advert.advertId)
                findNavController().navigate(direction)
            },
            onEditPostClickCallback =
            { advert ->
                /**
                 * ako treba jos neke paramtere dodati ih i prilagoditi kod ispod ili u PetsAdapter.kt
                 * val direction = <ime fragmenta za add i edit post>.actionPetsFragmentTo<ime fragmenta za add i edit post>(advert.advertId)
                 * findNavController().navigate(direction)
                 */
            },
            onDeletePostClickCallback =
            {
                advert ->
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.brisanje_oglasa))
                        .setMessage(getString(R.string.potvrda_brisanja_oglasa))
                        .setPositiveButton(getString(R.string.izbrisi)) { _, _ ->
                            viewModel.deleteAdvert(advert.advertId, isInternetAvailable(requireContext()))
                        }
                        .setNegativeButton(getString(R.string.odustani)) { dialog, _ ->
                            dialog.dismiss()
                        }.show()
            })
        binding.recyclerView.adapter = adapter
    }

    private fun setLiveDataObservers() {
        with(viewModel) {
            petsLiveData.observe(viewLifecycleOwner) { pets ->
                if (!pets.isNullOrEmpty()) {
                    if (pets != adapter.getPetsList()) {
                        adapter.updateData(pets, displayMyPets)
                    }
                } else {
                    adapter.updateData(listOf<Pet>(), displayMyPets)
                }
            }
            PetsDisplayStateLiveData.observe(viewLifecycleOwner) { state ->
                when (state) {
                    PetsDisplayState.LOADING -> {
                        showLoading()
                    }

                    PetsDisplayState.SUCCESSGET -> {
                        showPets()
                    }

                    PetsDisplayState.ERRORGET -> {
                        showNoPosts()
                        Snackbar.make(
                            binding.root,
                            "Došlo je do pogreške prilikom dohvaćanja oglasa",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    PetsDisplayState.SUCCESSDELETE -> {
                        Snackbar.make(
                            binding.root,
                            "Oglas je uspješno obrisan",
                            Snackbar.LENGTH_LONG
                        ).show()
                        getPets(filter, displayMyPets, null, true)
                    }
                    PetsDisplayState.ERRORDELETE -> {
                        Snackbar.make(
                            binding.root,
                            "Došlo je do pogreške prilikom brisanja oglasa",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    PetsDisplayState.NOPOSTS -> {
                        showNoPosts()
                    }
                }
            }
            filterPresentLiveData.observe(viewLifecycleOwner) { filterPresentLd ->
                if (filterPresentLd) {
                    binding.filterDisplay.visibility = View.VISIBLE
                } else {
                    binding.filterDisplay.visibility = View.GONE
                }
            }
        }

        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("filter")
            ?.observe(
                viewLifecycleOwner
            ) { filterJson ->
                getPets(Json.decodeFromString(filterJson), false, getString(R.string.filtriranje_po_ljubimcu))
                navController.currentBackStackEntry?.savedStateHandle?.remove<String>("filter")
            }
    }

    private fun buildUsernameInputDialog() {
        val userDialogBinding = FilterInputDialogBinding.inflate(layoutInflater)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        usernameDialog = builder.setPositiveButton("Pretraži") { dialog, _ ->
            val username = userDialogBinding.textInputEditText.text.toString()
            if (username.isNotEmpty()) {
                getPets(SearchFilter(korisnickoIme = username), false, username)
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
                getPets(SearchFilter(imeSklonista = shelterName), false, shelterName)
                dialog.dismiss()
            }
        }.setNegativeButton("Odustani") { dialog, _ ->
            dialog.dismiss()
        }.setView(R.layout.filter_input_dialog).setTitle("Unesite traženo ime skloništa").create()

        shelterDialogBinding.outlineTextFieldLayout.hint = "Ime skloništa"
        shelterDialog.setView(shelterDialogBinding.root)
    }
    private fun getPets(newFilter: SearchFilter?, gettingMyPets: Boolean, filterDisplayText: String?, reset: Boolean = false) {
        //filter has to be set to null if clear so that the live data gets updated below
        filter = newFilter
        displayMyPets = gettingMyPets
        if (filterDisplayText != null) {
            binding.currentFilter.text = filterDisplayText
            sharedPreferences.edit().putString("lastFilterTitle", filterDisplayText)
            .apply()
        }
        viewModel.getPets(
            isInternetAvailable(requireContext()),
            filter ?: SearchFilter(),
            displayMyPets,
            reset
        )
        viewModel.filterPresentLiveData.value = (filter != null || displayMyPets)
    }

    private fun showPets() {
        with(binding) {
            recyclerView.visibility = View.VISIBLE
            adapter.toggleLoadingSpinnerVisibility(false)
            noPostsDisplay.visibility = View.GONE
        }
    }

    private fun showLoading() {
        with(binding) {
            recyclerView.visibility = View.VISIBLE
            noPostsDisplay.visibility = View.GONE
            adapter.toggleLoadingSpinnerVisibility(true)
        }
    }

    private fun showNoPosts() {
        with(binding) {
            recyclerView.visibility = View.GONE
            adapter.toggleLoadingSpinnerVisibility(false)
            noPostsDisplay.visibility = View.VISIBLE
        }
    }
}