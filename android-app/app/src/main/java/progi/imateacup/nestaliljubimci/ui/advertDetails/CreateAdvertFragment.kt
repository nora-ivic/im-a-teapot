package progi.imateacup.nestaliljubimci.ui.advertDetails

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
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
    private lateinit var pickImage1: Button
    private lateinit var selectedImage1: ImageView
    private lateinit var pickImage2: Button
    private lateinit var selectedImage2: ImageView
    private lateinit var pickImage3: Button
    private lateinit var selectedImage3: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putString(ACCESS_TOKEN, null)
        }
        ApiModule.initRetrofit()
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
        val species = listOf("Ptica", "Mačka", "Pas", "Gušter","Zec","Glodavac","Zmija","Ostalo")
        val adapter1 = ArrayAdapter(requireContext(), R.layout.advert_chategory_list, species)
        (binding.petSpeciesField as? AutoCompleteTextView)?.setAdapter(adapter1)

        val categories = listOf("Izgubljen", "Pronađen", "Napušten", "U skloništu","Mrtav")
        val adapter2 = ArrayAdapter(requireContext(), R.layout.advert_chategory_list, categories)
        (binding.petCategoryField as? AutoCompleteTextView)?.setAdapter(adapter2)

        val sheltered = listOf("Da","Ne")
        val adapter3 = ArrayAdapter(requireContext(), R.layout.advert_chategory_list, sheltered)
        (binding.shelterField as? AutoCompleteTextView)?.setAdapter(adapter3)

        pickImage1 = binding.picture1
        selectedImage1 = binding.showPicture1

        pickImage1.setOnClickListener {
            val pickImg1 = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage1.launch(pickImg1)
        }

        pickImage2 = binding.picture2
        selectedImage2 = binding.showPicture2

        pickImage2.setOnClickListener {
            val pickImg2 = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage2.launch(pickImg2)
        }

        pickImage3 = binding.picture3
        selectedImage3 = binding.showPicture3

        pickImage3.setOnClickListener {
            val pickImg3 = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage3.launch(pickImg3)
        }
        binding.dateField.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .build()
        }

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
                    updateDescriptionField()
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

    private val changeImage1 =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                selectedImage1.setImageURI(imgUri)
            }
        }
    private val changeImage2 =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                selectedImage2.setImageURI(imgUri)
            }
        }
    private val changeImage3 =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                selectedImage3.setImageURI(imgUri)
            }
        }

}



