package progi.imateacup.nestaliljubimci.ui.advertDetails

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.DialogAddCommentBinding
import progi.imateacup.nestaliljubimci.databinding.FragmentAdvertDetailsBinding
import progi.imateacup.nestaliljubimci.model.networking.response.Advert
import progi.imateacup.nestaliljubimci.ui.authentication.LoginFragment
import progi.imateacup.nestaliljubimci.ui.authentication.PREFERENCES_NAME

class AdvertDetailsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var imagesAdapter: ImagesAdapter

    private var accessToken: String? = null
    private var _binding: FragmentAdvertDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<AdvertDetailsFragmentArgs>()

    private val advertDetailsViewModel: AdvertDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        accessToken = sharedPreferences.getString(LoginFragment.ACCESS_TOKEN, null)

        handleApiRequests()

        advertDetailsViewModel.setImageDir(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
    }
    private fun handleApiRequests() {
        advertDetailsViewModel.getAdvertDetails(args.advertId)
        //advertDetailsViewModel.getComments(args.advertId)
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
        with(requireActivity() as AppCompatActivity) {
            with(binding) {
                val dialog = buildDialog()
                commentButton.setOnClickListener {
                    dialog.show()
                }
                setSupportActionBar(toolAppBar)
                toolAppBar.setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        initRecyclerViewAdapter()
        displayAdvertDetails()
        displayImages()
        //displayComments()
    }

    private fun displayAdvertDetails() {
        with(binding) {

            commentButton.isVisible = accessToken != null

            advertDetailsViewModel.advertLiveData.observe(viewLifecycleOwner) { advert: Advert ->
                setAdvertDisplayValues(advert)
                shelterDetails.isVisible = advert.isInShelter
            }
            advertDetailsViewModel.advertFetchSuccessLiveData.observe(viewLifecycleOwner) { fetchSuccess ->
                if (!fetchSuccess) {
                    Snackbar.make(binding.root, R.string.advert_fetch_fail, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setAdvertDisplayValues(advert: Advert) {
        with(binding) {
            petSpeciesValue.text = advert.petSpecies.toString()
            petNameValue.text = advert.petName
            petColorValue.text = advert.petColor
            petAgeValue.text = advert.petAge.toString()
            petDescriptionValue.text = advert.description
            shelterNameValue.text = advert.shelterName
            shelterEmailValue.text = advert.shelterEmail
            shelterPhoneValue.text = advert.shelterPhone
        }
    }

    private fun displayComments() {
        with(binding) {

            advertDetailsViewModel.commentsLiveData.observe(viewLifecycleOwner) { comments ->
                if (!comments.isNullOrEmpty()) {
                    commentsAdapter.updateData(comments)
                    if (noCommentsMessage.isVisible) {
                        commentRecyclerView.isVisible = true
                        noCommentsMessage.isVisible = false
                    }
                } else {
                    commentRecyclerView.isVisible = false
                    noCommentsMessage.isVisible = true
                }
            }
            advertDetailsViewModel.commentAddedLiveData.observe(viewLifecycleOwner) { reviewAdded ->
                if (!reviewAdded) {
                    Snackbar.make(binding.root, R.string.comment_post_fail, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }

    private fun initRecyclerViewAdapter() {
        imagesAdapter = ImagesAdapter(emptyList())
        binding.imageRecycler.adapter = imagesAdapter

        commentsAdapter = CommentsAdapter(emptyList())
        binding.commentRecyclerView.adapter = commentsAdapter
    }

    private fun displayImages() {

        advertDetailsViewModel.advertLiveData.observe(viewLifecycleOwner) { advert ->
            if (advert.pictureLinks.isNotEmpty())
                imagesAdapter.updateData(advert.pictureLinks)
        }
    }

    private fun buildDialog(): BottomSheetDialog {
        val dialog = BottomSheetDialog(requireContext())
        val dialogAddCommentBinding = DialogAddCommentBinding.inflate(layoutInflater)
        dialog.setContentView(dialogAddCommentBinding.root)

        dialogAddCommentBinding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialogAddCommentBinding.submitButton.setOnClickListener {
            //detailedViewModel.advertComment(userId, advertId, text, pictureId, location)
            dialog.dismiss()
        }
        return dialog
    }

}