package progi.imateacup.nestaliljubimci.ui.advertDetails

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.AdvertFragmentBinding
import progi.imateacup.nestaliljubimci.networking.ApiModule
import progi.imateacup.nestaliljubimci.ui.authentication.PREFERENCES_NAME

class CreateAdvertFragment : Fragment() {

    companion object {
        const val ACCESS_TOKEN = "ACCESS_TOKEN"
    }

    private var _binding: AdvertFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CreateAdvertViewModel>()
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putString(ACCESS_TOKEN, null)
        }
        ApiModule.initRetrofit()
        /*val items = listOf(AdvertismentCategory.lost,AdvertismentCategory.abandoned,AdvertismentCategory.dead,AdvertismentCategory.found,AdvertismentCategory.sheltered)
        val arrayAdapter = ArrayAdapter(this, R.layout.advert_chategory_list, items)
        val category = findViewById<PetSpeciesField>(R.id.petSpeciesField)
        binding.MaterialAutoCompleteTextView.setAdapter(arrayAdapter)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AdvertFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAccessTokenObserver()
        setOnAdvertResultAction()
        initListeners()
    }

    private fun setAccessTokenObserver() {
        viewModel.accessTokenLiveData.observe(viewLifecycleOwner) { accessToken ->
            sharedPreferences.edit {
                putString(ACCESS_TOKEN, accessToken)
            }
        }
    }

    private fun setOnAdvertResultAction() {
        viewModel.createAdvertResultLiveData.observe(viewLifecycleOwner) { isAdvertSuccessful ->
            if (isAdvertSuccessful) {
                val direction = CreateAdvertFragmentDirections.actionCreateAdvertFragmentToPetsFragment()
                findNavController().navigate(direction)
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.advert_unsuccessful,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initListeners() {
        with(binding) {

            petNameField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updatePetNameField()
                }
            }
            petColorField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updatePetColorField()
                }
            }
            petAgeField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updatePetAgeField()
                }
            }
            descriptionField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updatePetAgeField()
                }
            }


        }
    }
    private fun updatePetNameField() {
        with(binding) {
            petNameFieldLayout.error = null
        }
    }
    private fun updatePetAgeField() {
        with(binding) {
            petNameFieldLayout.error = null
        }
    }
    private fun updatePetColorField() {
        with(binding) {
            petNameFieldLayout.error = null
        }
    }
    private fun updateDescriptionField() {
        with(binding) {
            petNameFieldLayout.error = null
        }
    }
}



