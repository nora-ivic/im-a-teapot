package progi.imateacup.nestaliljubimci.ui.createAdvert

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.CreateAdvertFragmentBinding
import progi.imateacup.nestaliljubimci.model.networking.enums.AdvertisementCategory
import progi.imateacup.nestaliljubimci.model.networking.enums.PetSpecies
import progi.imateacup.nestaliljubimci.ui.advertDetails.AdvertDetailsFragmentDirections
import progi.imateacup.nestaliljubimci.util.FileUtil
import progi.imateacup.nestaliljubimci.util.getRealPathFromURI
import java.io.File

class CreateAdvertFragment : Fragment() {

    private var file: File? = null
    private lateinit var snapAnImage: ActivityResultLauncher<Uri>
    private lateinit var pickAnImage: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var imageUri: Uri

    private val TEMPORARY_COMENT_TEXT_KEY = "temporary_comment_text"
    private var temporaryMessage: String = ""

    private var _binding: CreateAdvertFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CreateAdvertViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleAddImage()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateAdvertFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnAdvertResultAction()
        initListeners()
        init()
    }

    private fun setOnAdvertResultAction() {
        viewModel.advertAddedLiveData.observe(viewLifecycleOwner) { isAdvertSuccessful ->
            if (isAdvertSuccessful) {
                val direction =
                    CreateAdvertFragmentDirections.actionCreateAdvertFragmentToPetsFragment()
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


    private fun init() {
        with(binding) {
            val species =
                listOf("Ptica", "Mačka", "Pas", "Gušter", "Zec", "Glodavac", "Zmija", "Ostalo")
            val adapter1 = ArrayAdapter(requireContext(), R.layout.advert_chategory_list, species)
            (petSpeciesField as? AutoCompleteTextView)?.setAdapter(adapter1)

            val categories = listOf("Izgubljen", "Pronađen", "Napušten", "U skloništu", "Mrtav")
            val adapter2 = ArrayAdapter(requireContext(), R.layout.advert_chategory_list, categories)

            val defaultCategory = "Izgubljen"
            val defaultCategoryPosition = categories.indexOf(defaultCategory)
            if (defaultCategoryPosition != -1) {
                (petCategoryField as? AutoCompleteTextView)?.setText(defaultCategory, false)
            }
            (petCategoryField as? AutoCompleteTextView)?.setAdapter(adapter2)
        }
    }


    private fun initListeners() {
        with(binding) {

            dateField.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .build()
            }

            addImageButton.setOnClickListener {
                showAddPictureAlertDialog()
            }

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

            submitButton.setOnClickListener {
                val selectedSpecies = petSpeciesField.text.toString().let {
                    speciesMapping[it] ?: run {
                        null
                    }
                }
                val selectedAge = petAgeField.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull()

                viewModel.advertAdvert(
                    advert_category = categoryMapping[petCategoryField.text.toString()]!!,
                    pet_name = petNameField.text.toString(),
                    pet_species = selectedSpecies,
                    pet_color = petColorField.text.toString(),
                    pet_age = selectedAge,
                    date_time_lost = "2024-01-15T15:12:57.584Z",
                    location_lost = "",
                    description = descriptionField.text.toString(),
                )
            }



        }
    }

    private fun showAddPictureAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.select_method)
            .setMessage(R.string.picture_change_instruction)
            .setPositiveButton(R.string.camera) { _: DialogInterface, _: Int -> takeANewProfilePicture() }
            .setNegativeButton(R.string.gallery) { _: DialogInterface, _: Int -> chooseProfilePictureFromGallery() }
            .show()
    }

    private fun takeANewProfilePicture() {
        file = FileUtil.createImageFile(requireContext())
        if (file != null) {
            imageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().applicationContext.packageName}.provider",
                file!!
            )
        }
        if (this::imageUri.isInitialized) {
            snapAnImage.launch(imageUri)
        }
    }

    private fun chooseProfilePictureFromGallery() {
        pickAnImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun updatePetNameField() {
        with(binding) {
            petNameFieldLayout.error = null
        }
    }

    private fun handleAddImage() {
        snapAnImageRegister()
        pickAnImageRegister()
    }

    private fun snapAnImageRegister() {

        snapAnImage =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { pictureSaved ->
                if (pictureSaved) {
                    uploadImage()
                } else {
                    Log.e("SavePicture", "Picture not saved")
                }
            }
    }

    private fun pickAnImageRegister() {
        pickAnImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val path = getRealPathFromURI(uri, requireContext())
                Log.i("PATH", path.toString())
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, flag)
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        // advertDetailsViewModel.uploadImage(imageUri.toFile())
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

    val categoryMapping = mapOf(
        "Izgubljen" to AdvertisementCategory.lost,
        "Pronađen" to AdvertisementCategory.found,
        "Napušten" to AdvertisementCategory.abandoned,
        "U skloništu" to AdvertisementCategory.sheltered,
        "Mrtav" to AdvertisementCategory.dead,
    )

    val speciesMapping = mapOf(
        "Ptica" to PetSpecies.bird,
        "Mačka" to PetSpecies.cat,
        "Pas" to PetSpecies.dog,
        "Gušter" to PetSpecies.lizard,
        "Ostalo" to PetSpecies.other,
        "Zec" to PetSpecies.rabbit,
        "Glodavac" to PetSpecies.rodent,
        "Zmija" to PetSpecies.snake,
    )
}



