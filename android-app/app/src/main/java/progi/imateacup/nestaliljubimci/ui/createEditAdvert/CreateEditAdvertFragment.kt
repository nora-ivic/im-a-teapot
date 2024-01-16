package progi.imateacup.nestaliljubimci.ui.createEditAdvert

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
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
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import okio.Path.Companion.toPath
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.CreateAdvertFragmentBinding
import progi.imateacup.nestaliljubimci.model.networking.enums.AdvertisementCategory
import progi.imateacup.nestaliljubimci.model.networking.enums.PetSpecies
import progi.imateacup.nestaliljubimci.ui.advertDetails.PFP_URI_NAME_DECORATOR
import progi.imateacup.nestaliljubimci.ui.authentication.PREFERENCES_NAME
import progi.imateacup.nestaliljubimci.util.FileUtil
import progi.imateacup.nestaliljubimci.util.getRealPathFromURI
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateEditAdvertFragment : Fragment() {

    private var file: File? = null
    private lateinit var snapAnImage: ActivityResultLauncher<Uri>
    private lateinit var pickAnImage: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var imageUri: Uri
    private lateinit var renamedFile: File
    private lateinit var datePicker: MaterialDatePicker<Long>

    private lateinit var sharedPreferences: SharedPreferences

    private val args by navArgs<CreateEditAdvertFragmentArgs>()
    private var isEditing = false

    private var _binding: CreateAdvertFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CreateEditAdvertViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        handleAddImage()
        initDatePicker()
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
        autofill()
        setOnAdvertResultAction()
        setSubmitButton()
        initListeners()
        init()
    }

    private fun autofill() {
        if (args.advertId != -1) {
            isEditing = true
            viewModel.getAdvertDetails(args.advertId)

            viewModel.advertFetchSuccessLiveData.observe(viewLifecycleOwner) { fetchSuccess ->
                if (!fetchSuccess) {
                    Snackbar.make(binding.root, R.string.advert_fetch_fail, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }

            viewModel.advertLiveData.observe(viewLifecycleOwner) { advert ->
                with(binding) {
                    if (advert != null) {
                        petNameField.setText(advert.petName)
                        petColorField.setText(advert.petColor)
                        descriptionField.setText(advert.description)
                        petAgeField.setText(advert.petAge?.toString() ?: "")

                        if (advert.dateTimeLost != null) {
                            val inputFormat =
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            try {
                                val date = advert.dateTimeLost?.let { inputFormat.parse(it) }
                                dateField.setText(outputFormat.format(date!!))
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                        }

                        val pictureLinks = advert.pictureLinks
                        val uriList: List<Uri> = pictureLinks.map { Uri.parse(it) }
                        viewModel.setImageLinks(uriList)

                        viewModel.setAdvertCoordinates(advert.locationLost)

                        val species =
                            listOf(
                                "Ptica",
                                "Mačka",
                                "Pas",
                                "Gušter",
                                "Zec",
                                "Glodavac",
                                "Zmija",
                                "Ostalo"
                            )
                        val adapter1 =
                            ArrayAdapter(requireContext(), R.layout.advert_chategory_list, species)
                        (petSpeciesField as? AutoCompleteTextView)?.setAdapter(adapter1)

                        val categories =
                            listOf("Izgubljen", "Pronađen", "Napušten", "U skloništu", "Mrtav")
                        val adapter2 =
                            ArrayAdapter(
                                requireContext(),
                                R.layout.advert_chategory_list,
                                categories
                            )

                        val category =
                            categoryMapping.entries.find { it.value == advert.category }?.key ?: ""
                        val categoryPosition = categories.indexOf(category)
                        if (categoryPosition != -1) {
                            (petCategoryField as? AutoCompleteTextView)?.setText(category, false)
                        }
                        (petCategoryField as? AutoCompleteTextView)?.setAdapter(adapter2)

                        val specie =
                            speciesMapping.entries.find { it.value == advert.petSpecies }?.key ?: ""
                        val speciesPosition = species.indexOf(specie)
                        if (speciesPosition != -1) {
                            (petSpeciesField as? AutoCompleteTextView)?.setText(specie, false)
                        }
                        (petCategoryField as? AutoCompleteTextView)?.setAdapter(adapter2)

                        submitButton.text = getString(R.string.edit_advert)
                    }
                }
            }
        }
    }

    private fun setOnAdvertResultAction() {
        viewModel.advertIdLiveData.observe(viewLifecycleOwner) { advertId ->
            if (advertId != null) {
                val direction =
                    CreateEditAdvertFragmentDirections.actionCreateAdvertFragmentToDetailsFragment(
                        advertId
                    )
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
            val adapter2 =
                ArrayAdapter(requireContext(), R.layout.advert_chategory_list, categories)

            val defaultCategory = "Izgubljen"
            val defaultCategoryPosition = categories.indexOf(defaultCategory)
            if (defaultCategoryPosition != -1) {
                (petCategoryField as? AutoCompleteTextView)?.setText(defaultCategory, false)
            }
            (petCategoryField as? AutoCompleteTextView)?.setAdapter(adapter2)

            viewModel.imageUploadSuccessLiveData.observe(viewLifecycleOwner) { success ->
                if (!success) {
                    Snackbar.make(
                        binding.root,
                        "Došlo je do pogreške prilikom dodavanja slike",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
        observeCoordinates()
    }

    private fun initDatePicker() {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Odaberite datum nestanka")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()
    }

    private fun initListeners() {
        val inputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        with(binding) {

            dateField.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus)
                    datePicker.show(parentFragmentManager, "datePicker")
            }

            dateField.setOnClickListener {
                datePicker.show(parentFragmentManager, "datePicker")
            }

            datePicker.addOnPositiveButtonClickListener {
                dateField.setText(datePicker.headerText)
            }

            petNameField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updatePetNameField()
                }
            }
            petNameField.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    setSubmitButton()
                }
            })
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
            petSpeciesField.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    setSubmitButton()
                }
            })
            descriptionField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updateDescriptionField()
                }
            }

            descriptionField.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    setSubmitButton()
                }
            })

            submitButton.setOnClickListener {
                val selectedSpecies = petSpeciesField.text.toString().let {
                    speciesMapping[it] ?: run {
                        null
                    }
                }
                val selectedAge =
                    petAgeField.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull()

                var formattedDate: String? = null
                if (dateField.text.toString().isNotBlank()) {
                    val date: Date?
                    try {
                        date = inputFormat.parse(dateField.text.toString())
                        formattedDate = outputFormat.format(date!!)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }

                val uriList: List<Uri> = viewModel.imageLinksLiveData.value ?: emptyList()
                val stringList: List<String> = uriList.map { uri -> uri.toString() }

                if (args.advertId == -1) {
                    viewModel.advertAdvert(
                        advert_category = categoryMapping[petCategoryField.text.toString()]!!,
                        pet_name = petNameField.text.toString(),
                        pet_species = selectedSpecies,
                        pet_color = petColorField.text.toString().ifBlank { null },
                        pet_age = selectedAge,
                        date_time_lost = formattedDate,
                        location_lost = viewModel.advertCoordinatesLiveData.value,
                        description = descriptionField.text.toString().ifBlank { null },
                        pictureLinks = stringList
                    )
                } else {
                    viewModel.editAdvert(
                        advert_category = categoryMapping[petCategoryField.text.toString()]!!,
                        pet_name = petNameField.text.toString(),
                        pet_species = selectedSpecies,
                        pet_color = petColorField.text.toString().ifBlank { null },
                        pet_age = selectedAge,
                        date_time_lost = formattedDate,
                        location_lost = viewModel.advertCoordinatesLiveData.value,
                        description = descriptionField.text.toString().ifBlank { null },
                        pictureLinks = stringList,
                        advert_id = args.advertId
                    )
                }
            }

            addLocationButton.setOnClickListener {
                if (viewModel.advertCoordinatesLiveData.value != null) {
                    viewModel.setAdvertCoordinates(null)
                } else {
                    val direction =
                        CreateEditAdvertFragmentDirections.actionCreateAdvertFragmentToMapSelectLocationFragment()
                    findNavController().navigate(direction)
                }
            }

            removeImagesButton.setOnClickListener {
                val currentImageLinks = viewModel.imageLinksLiveData.value ?: emptyList()

                if (currentImageLinks.isNotEmpty()) {
                    viewModel.setImageLinks(emptyList())
                } else {
                    removeImagesButton.visibility = View.GONE
                }
            }

            addImageButton.setOnClickListener {
                showAddPictureAlertDialog()
            }

            viewModel.imageLinksLiveData.observe(viewLifecycleOwner) { pictureUrls ->
                setSubmitButton()
                if (pictureUrls.size == 1) {
                    imageInfo.visibility = View.VISIBLE
                    imageInfo.text = getString(R.string.one_image)
                    imageInfo.visibility = View.VISIBLE
                    addImageButton.isEnabled = true
                    removeImagesButton.text = getString(R.string.remove_image)
                    removeImagesButton.visibility = View.VISIBLE
                } else if ((pictureUrls.size == 2)) {
                    imageInfo.text = getString(R.string.two_images)
                    imageInfo.visibility = View.VISIBLE
                    addImageButton.isEnabled = true
                    removeImagesButton.text = getString(R.string.remove_images)
                    removeImagesButton.visibility = View.VISIBLE
                } else if ((pictureUrls.size > 2)) {
                    imageInfo.text = getString(R.string.three_images)
                    imageInfo.visibility = View.VISIBLE
                    addImageButton.isEnabled = false
                    removeImagesButton.text = getString(R.string.remove_images)
                    removeImagesButton.visibility = View.VISIBLE
                } else {
                    imageInfo.visibility = View.GONE
                    addImageButton.isEnabled = true
                    removeImagesButton.visibility = View.GONE
                }
            }

            viewModel.advertCoordinatesLiveData.observe(viewLifecycleOwner) { advertCoordinates ->
                if (advertCoordinates != null) {
                    locationInfo.visibility = View.VISIBLE
                    addLocationButton.text = getString(R.string.remove_location)
                } else {
                    locationInfo.visibility = View.GONE
                    addLocationButton.text = getString(R.string.add_location)
                }
            }

        }
    }

    private fun setSubmitButton() {
        with(binding) {
            val uriList: List<Uri> = viewModel.imageLinksLiveData.value ?: emptyList()
            val stringList: List<String> = uriList.map { uri -> uri.toString() }

            val selectedSpecies = petSpeciesField.text.toString().let {
                speciesMapping[it] ?: run {
                    null
                }
            }
            val petName = petNameField.text.toString()
            val description = descriptionField.text.toString()
            binding.submitButton.isEnabled =
                (petName.isNotBlank() || selectedSpecies != null || description.isNotBlank() || stringList.isNotEmpty())
        }
    }

    private fun observeCoordinates() {
        val navController = findNavController()

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("coordinates")
            ?.observe(
                viewLifecycleOwner
            ) { coordinates ->
                viewModel.setAdvertCoordinates(coordinates)
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
                    FileUtil.getImageFile(requireContext())

                    //rename the file so profile pictures for different emails can be saved
                    renamedFile = File(
                        requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "${"image"}${System.currentTimeMillis()}${PFP_URI_NAME_DECORATOR}.jpg"
                    )
                    //file is never null since the snapAnImage.launch() is only called if the file was created successfully
                    file!!.renameTo(renamedFile)

                    //remember the profile picture for the given email
                    sharedPreferences.edit {
                        putString(
                            createLiteral(),
                            renamedFile.path.toString()
                        )
                    }

                    uploadImage()
                } else {
                    Log.e("SavePicture", "Picture not saved")
                }
            }
    }

    private fun createLiteral(): String {
        return "${"image"}${PFP_URI_NAME_DECORATOR}"
    }

    private fun pickAnImageRegister() {
        pickAnImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val path = getRealPathFromURI(uri, requireContext())
                sharedPreferences.edit { putString(createLiteral(), path) }
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, flag)
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        val picturePath =
            sharedPreferences.getString(createLiteral(), null)?.toPath()
        if (picturePath != null) {
            viewModel.uploadImage(picturePath.toFile())
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



