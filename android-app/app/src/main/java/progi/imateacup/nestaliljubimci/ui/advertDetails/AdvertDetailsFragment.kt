package progi.imateacup.nestaliljubimci.ui.advertDetails

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.DialogAddCommentBinding
import progi.imateacup.nestaliljubimci.databinding.FragmentAdvertDetailsBinding
import com.google.android.material.snackbar.Snackbar
import okio.Path.Companion.toPath
import progi.imateacup.nestaliljubimci.model.networking.enums.AdvertisementCategory
import progi.imateacup.nestaliljubimci.model.networking.enums.DisplayState
import progi.imateacup.nestaliljubimci.model.networking.enums.PetSpecies
import progi.imateacup.nestaliljubimci.model.networking.response.Advert
import progi.imateacup.nestaliljubimci.ui.authentication.LoginFragment
import progi.imateacup.nestaliljubimci.ui.authentication.PREFERENCES_NAME
import progi.imateacup.nestaliljubimci.util.FileUtil
import progi.imateacup.nestaliljubimci.util.getRealPathFromURI
import progi.imateacup.nestaliljubimci.util.isInternetAvailable
import java.io.File

const val PFP_URI_NAME_DECORATOR = "MessageImage"

class AdvertDetailsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var renamedFile: File

    private var temporaryMessage: String = ""

    private var accessToken: String? = null
    private var _binding: FragmentAdvertDetailsBinding? = null

    private val binding get() = _binding!!

    private var file: File? = null
    private lateinit var snapAnImage: ActivityResultLauncher<Uri>
    private lateinit var pickAnImage: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var imageUri: Uri

    private val args by navArgs<AdvertDetailsFragmentArgs>()

    private val advertDetailsViewModel: AdvertDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        accessToken = sharedPreferences.getString(LoginFragment.ACCESS_TOKEN, null)

        handleApiRequests()
        handleAddImage()

        advertDetailsViewModel.setImageDir(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
    }

    private fun handleApiRequests() {
        advertDetailsViewModel.getAdvertDetails(args.advertId)
        advertDetailsViewModel.getComments(args.advertId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdvertDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun init() {
        with(binding) {
            topAppBarDetails.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            val dialog = buildCommentDialog()
            commentButton.setOnClickListener {
                dialog.show()
            }
        }
        observeCoordinates()
        initRecyclerViews()
        displayAdvertDetails()
        displayImages()
        displayComments()
    }

    private fun displayComments() {
        with(advertDetailsViewModel) {
            commentsLiveData.observe(viewLifecycleOwner) { comments ->
                if (!comments.isNullOrEmpty() && comments != commentsAdapter.getCommentList()) {
                    commentsAdapter.updateData(comments)
                }
            }
            commentsDisplayStateLiveData.observe(viewLifecycleOwner) { state ->
                when (state) {
                    DisplayState.LOADING -> {
                        showLoading()
                    }

                    DisplayState.SUCCESSGET -> {
                        showComments()
                    }

                    DisplayState.ERRORGET -> {
                        showNoPosts()
                        Snackbar.make(
                            binding.root,
                            "Došlo je do pogreške prilikom dohvaćanja komentara",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    DisplayState.NOPOSTS -> {
                        showNoPosts()
                    }

                    else -> {
                        showNoPosts()
                    }
                }
            }
            advertDetailsViewModel.commentAddedLiveData.observe(viewLifecycleOwner) { commentAdded ->
                if (!commentAdded) {
                    Snackbar.make(binding.root, R.string.comment_post_fail, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun displayAdvertDetails() {
        with(binding) {

            commentButton.isVisible = accessToken != null

            advertDetailsViewModel.advertLiveData.observe(viewLifecycleOwner) { advert: Advert? ->
                if (advert != null) {
                    setAdvertDisplayValues(advert)
                    shelterDetails.isVisible = advert.isInShelter
                }
            }
            advertDetailsViewModel.advertFetchSuccessLiveData.observe(viewLifecycleOwner) { fetchSuccess ->
                if (!fetchSuccess) {
                    Snackbar.make(binding.root, R.string.advert_fetch_fail, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun observeCoordinates() {
        val navController = findNavController()
        val dialog = buildCommentDialog()

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("coordinates")
            ?.observe(
                viewLifecycleOwner
            ) { coordinates ->
                advertDetailsViewModel.setMessageCoordinates(coordinates)
                dialog.show()
            }
    }

    private fun setAdvertDisplayValues(advert: Advert) {
        val oldTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val newTimeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy.")
        with(binding) {
            petStatusValue.text = categoryMapping.entries.find { it.value == advert.category }?.key
                ?: "Nepoznato"
            petSpeciesValue.text =
                speciesMapping.entries.find { it.value == advert.petSpecies }?.key
                    ?: "Nepoznato"
            petNameValue.text = advert.petName ?: "Nepoznato"
            petColorValue.text = advert.petColor ?: "Nepoznato"
            petAgeValue.text =
                if (advert.petAge != null) advert.petAge.toString() + " godina" else "Nepoznato"
            petDescriptionValue.text = advert.description ?: "Nepoznato"
            shelterNameValue.text = advert.shelterName ?: "Nepoznato"
            shelterEmailValue.text = advert.shelterEmail ?: "Nepoznato"
            shelterPhoneValue.text = advert.shelterPhone ?: "Nepoznato"
            timeLostValue.text = if (advert.dateTimeLost != null)
                LocalDateTime.parse(advert.dateTimeLost, oldTimeFormat)
                    .format(newTimeFormat) else "Nepoznato"
            if (advert.locationLost == null) {
                locationLostValue.apply {
                    text = "Nepoznato"
                    setTextColor(resources.getColor(R.color.black, null))
                }
            } else {
                locationLostValue.setOnClickListener {
                    val direction =
                        AdvertDetailsFragmentDirections.actionAdvertDetailsViewToMapDisplayLocationFragment(
                            advert.locationLost
                        )
                    findNavController().navigate(direction)
                }


            }
        }
    }

    private fun initRecyclerViews() {
        with(binding) {
            imagesAdapter = ImagesAdapter(emptyList())
            imageRecycler.adapter = imagesAdapter

            commentsAdapter = CommentsAdapter(emptyList())
            commentRecyclerView.adapter = commentsAdapter

            val layoutManager = LinearLayoutManager(context)
            commentRecyclerView.layoutManager = layoutManager
            commentRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        if (isInternetAvailable(requireContext())) {
                            advertDetailsViewModel.getComments(args.advertId)
                        }
                    }
                }
            })
        }
    }

    private fun displayImages() {

        advertDetailsViewModel.advertLiveData.observe(viewLifecycleOwner) { advert ->
            if (advert != null) {
                if (advert.pictureLinks.isNotEmpty()) {
                    imagesAdapter.updateData(advert.pictureLinks)
                    binding.imageRecycler.visibility = View.VISIBLE
                } else {
                    binding.imageRecycler.visibility = View.GONE
                }
            }
        }
    }

    private fun buildCommentDialog(): BottomSheetDialog {

        val dialog = BottomSheetDialog(requireContext())
        val dialogAddCommentBinding = DialogAddCommentBinding.inflate(layoutInflater)

        dialog.setContentView(dialogAddCommentBinding.root)

        dialogAddCommentBinding.addImageButton.setOnClickListener {
            if (advertDetailsViewModel.imageLinkLiveData.value != null) {
                advertDetailsViewModel.setImageLink(null)
            } else {
                showAddPictureAlertDialog()
            }
        }

        advertDetailsViewModel.imageUploadSuccessLiveData.observe(viewLifecycleOwner) { success ->
            if (!success) {
                Snackbar.make(
                    binding.root,
                    "Došlo je do pogreške prilikom dodavanja slike",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        if (temporaryMessage.isNotEmpty()) {
            dialogAddCommentBinding.messageInput.setText(temporaryMessage)
        }

        advertDetailsViewModel.messageCoordinatesLiveData.observe(viewLifecycleOwner) { messageCoordinates ->
            if (messageCoordinates != null) {
                dialogAddCommentBinding.locationInfo.visibility = View.VISIBLE
                dialogAddCommentBinding.addLocationButton.text = getString(R.string.remove_location)
            } else {
                dialogAddCommentBinding.locationInfo.visibility = View.GONE
                dialogAddCommentBinding.addLocationButton.text = getString(R.string.add_location)
            }
        }

        advertDetailsViewModel.imageLinkLiveData.observe(viewLifecycleOwner) { pictureUrl ->
            if (pictureUrl != null) {
                dialogAddCommentBinding.imageInfo.visibility = View.VISIBLE
                dialogAddCommentBinding.addImageButton.text = getString(R.string.remove_image)
            } else {
                dialogAddCommentBinding.imageInfo.visibility = View.GONE
                dialogAddCommentBinding.addImageButton.text = getString(R.string.add_message_image)
            }
        }

        dialogAddCommentBinding.addLocationButton.setOnClickListener {

            temporaryMessage = dialogAddCommentBinding.messageInput.text.toString()

            if (advertDetailsViewModel.messageCoordinatesLiveData.value != null) {
                advertDetailsViewModel.setMessageCoordinates(null)
            } else {
                dialog.dismiss()
                val direction =
                    AdvertDetailsFragmentDirections.actionAdvertDetailsViewToMapSelectLocationFragment()
                findNavController().navigate(direction)
            }
        }

        dialogAddCommentBinding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialogAddCommentBinding.submitButton.setOnClickListener {
            val uriLink = advertDetailsViewModel.imageLinkLiveData.value
            val uriList = if (uriLink != null) listOf(uriLink) else emptyList()
            val stringList: List<String> = uriList.map { uri -> uri.toString() }

            advertDetailsViewModel.advertComment(
                advertId = args.advertId,
                text = dialogAddCommentBinding.messageInput.text.toString(),
                pictureLinks = stringList,
                location = advertDetailsViewModel.messageCoordinatesLiveData.value.orEmpty()
            )
            dialog.dismiss()
        }


        return dialog
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
                        "${args.advertId}${System.currentTimeMillis()}${PFP_URI_NAME_DECORATOR}.jpg"
                    )
                    //file is never null since the snapAnImage.launch() is only called if the file was created successfully
                    file!!.renameTo(renamedFile)

                    //remember the profile picture for the given email
                    sharedPreferences.edit {
                        putString(
                            createLiteral(args.advertId.toString()),
                            renamedFile.path.toString()
                        )
                    }

                    uploadImage()
                } else {
                    Log.e("SavePicture", "Picture not saved")
                }
            }
    }

    private fun createLiteral(advertId: String): String {
        return "${advertId}${PFP_URI_NAME_DECORATOR}"
    }

    private fun pickAnImageRegister() {
        pickAnImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val path = getRealPathFromURI(uri, requireContext())
                sharedPreferences.edit { putString(createLiteral(args.advertId.toString()), path) }
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, flag)
                uploadImage()
            }
        }
    }


    private fun uploadImage() {
        val picturePath =
            sharedPreferences.getString(createLiteral(args.advertId.toString()), null)?.toPath()
        if (picturePath != null) {
            advertDetailsViewModel.uploadImage(picturePath.toFile())
        }
    }

    private fun showComments() {
        with(binding) {
            commentRecyclerView.visibility = View.VISIBLE
            commentsAdapter.toggleLoadingSpinnerVisibility(false)
            noCommentsMessage.visibility = View.GONE
        }
    }

    private fun showLoading() {
        with(binding) {
            commentRecyclerView.visibility = View.VISIBLE
            commentsAdapter.toggleLoadingSpinnerVisibility(true)
            noCommentsMessage.visibility = View.GONE
        }
    }

    private fun showNoPosts() {
        with(binding) {
            commentRecyclerView.visibility = View.GONE
            commentsAdapter.toggleLoadingSpinnerVisibility(false)
            noCommentsMessage.visibility = View.VISIBLE
        }
    }

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

    val categoryMapping = mapOf(
        "Izgubljen" to AdvertisementCategory.lost,
        "Pronađen" to AdvertisementCategory.found,
        "Napušten" to AdvertisementCategory.abandoned,
        "U skloništu" to AdvertisementCategory.sheltered,
        "Mrtav" to AdvertisementCategory.dead,
    )
}