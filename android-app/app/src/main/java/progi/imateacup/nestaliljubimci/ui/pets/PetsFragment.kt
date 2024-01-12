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
                viewModel.getPets(isInternetAvailable(requireContext()), SearchFilter())
                viewModel.filterPresentLiveData.value = false
                filter = SearchFilter()
                currentFilter.text = ""
                filterDisplay.visibility = View.GONE
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
                sharedPreferences.edit()
                    .putString("lastFilterTitle", getString(R.string.mojiOglasi))
                    .apply()
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
                topAppBar.menu.removeItem(R.id.pronadeni)
                topAppBar.menu.removeItem(R.id.trazi_po_sklonistu)
                topAppBar.menu.removeItem(R.id.prekinutoTrazenje)
                topAppBar.menu.removeItem(R.id.uSklonistu)
                topAppBar.menu.removeItem(R.id.uginuli)
            }

            topAppBarPets.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.izgubljeni -> {
                        filterAndGetForCategory(
                            AdvertisementCategory.lost.toString(),
                            getString(R.string.izgubljeni)
                        )
                        true
                    }

                    R.id.pronadeni -> {
                        filterAndGetForCategory(
                            AdvertisementCategory.found.toString(),
                            getString(R.string.pronadeni)
                        )
                        true
                    }

                    R.id.prekinutoTrazenje -> {
                        filterAndGetForCategory(
                            AdvertisementCategory.dead.toString(),
                            getString(R.string.prekinutoTrazenje)
                        )
                        true
                    }

                    R.id.uSklonistu -> {
                        filterAndGetForCategory(
                            AdvertisementCategory.sheltered.toString(),
                            getString(R.string.uSklonistu)
                        )
                        true
                    }

                    R.id.uginuli -> {
                        filterAndGetForCategory(
                            AdvertisementCategory.dead.toString(),
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

    private fun filterAndGetForCategory(category: String, title: String? = "") {
        binding.currentFilter.text = title
        sharedPreferences.edit().putString("lastFilterTitle", title)
            .apply()
        filter = SearchFilter(kategorijaOglasa = category)
        viewModel.getPets(isInternetAvailable(requireContext()), filter ?: SearchFilter())
        viewModel.filterPresentLiveData.value = true
        binding.filterDisplay.visibility = View.VISIBLE
    }

    private fun initRecyclerViewAdapter() {
        adapter = PetsAdapter(emptyList()) { advert ->
            val direction =
                PetsFragmentDirections.actionPetsFragmentToDetailedViewFragment(advertId = advert.advertId)
            findNavController().navigate(direction)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun setLiveDataObservers() {
        with(viewModel) {
            petsLiveData.observe(viewLifecycleOwner) { pets ->
                if (!pets.isNullOrEmpty() && pets != adapter.getPetsList()) {
                    adapter.updateData(pets)
                }
            }
            PetsDisplayStateLiveData.observe(viewLifecycleOwner) { state ->
                when (state) {
                    PetsDisplayState.LOADING -> {
                        showLoading()
                    }

                    PetsDisplayState.SUCCESS -> {
                        showPets()
                    }

                    PetsDisplayState.ERROR -> {
                        showNoPosts()
                        Snackbar.make(
                            binding.root,
                            "Došlo je do pogreške prilikom dohvaćanja oglasa",
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
                filter = Json.decodeFromString(filterJson)
                binding.filterDisplay.visibility = View.VISIBLE
                binding.currentFilter.text = getString(R.string.filtriranje_po_ljubimcu)
                viewModel.getPets(isInternetAvailable(requireContext()), filter ?: SearchFilter())
                viewModel.filterPresentLiveData.value = true
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
                sharedPreferences.edit().putString("lastFilterTitle", username)
                    .apply()
                viewModel.getPets(isInternetAvailable(requireContext()), filter ?: SearchFilter())
                viewModel.filterPresentLiveData.value = true
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
                sharedPreferences.edit().putString("lastFilterTitle", shelterName)
                    .apply()
                viewModel.getPets(isInternetAvailable(requireContext()), filter ?: SearchFilter())
                viewModel.filterPresentLiveData.value = true
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