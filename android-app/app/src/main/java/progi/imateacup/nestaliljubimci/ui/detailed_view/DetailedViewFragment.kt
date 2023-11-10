package progi.imateacup.nestaliljubimci.ui.detailed_view

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
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.DialogAddCommentBinding
import progi.imateacup.nestaliljubimci.databinding.FragmentDetailedViewBinding
import progi.imateacup.nestaliljubimci.model.networking.entities.Advert
import progi.imateacup.nestaliljubimci.model.networking.entities.Pet
import progi.imateacup.nestaliljubimci.model.networking.entities.Shelter

class DetailedViewFragment : Fragment() {

    private lateinit var adapter: CommentsAdapter

    private var _binding: FragmentDetailedViewBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DetailedViewFragmentArgs>()

    private val detailedViewModel: DetailedViewModel by viewModels()

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var accessToken: String
    private lateinit var client: String
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("Adds", Context.MODE_PRIVATE)
        //getSessionInfo()
        handleApiRequests()
        detailedViewModel.setImageDir(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
    }

    /*
        private fun getSessionInfo() {
            accessToken = sharedPreferences.getString(LoginFragment.ACCESS_TOKEN, "")!!
            client = sharedPreferences.getString(LoginFragment.CLIENT, "")!!
            uid = sharedPreferences.getString(LoginFragment.UID, "")!!
        }
    */


    private fun handleApiRequests() {
        detailedViewModel.getAdvertInfo(args.advertId)
        detailedViewModel.getComments(args.advertId)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailedViewBinding.inflate(inflater, container, false)
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

                //build the dialog only once so multiple instances can't be created at the same time
                val dialog = buildDialog()
                commentButton.setOnClickListener {
                    dialog.show()
                }
                /*
                    setSupportActionBar(toolAppBar)

                    toolAppBar.setNavigationOnClickListener {
                        findNavController().popBackStack()
                    }`
                 */
            }

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = ""
        }
        displayAdvertDetails()
        displayComments()
    }

    private fun displayAdvertDetails() {

        detailedViewModel.advertLiveData.observe(viewLifecycleOwner) { advert ->
            if (advert != null) {
                setAdvertDisplayValues(advert)

                detailedViewModel.getPet(advert.petId)
                detailedViewModel.getShelter(advert.shelterId)
            }
        }
        detailedViewModel.petLiveData.observe(viewLifecycleOwner) { pet ->
            if (pet != null) {
                setPetDisplayValues(pet)
            }
        }
        detailedViewModel.shelterLiveData.observe(viewLifecycleOwner) { shelter ->
            if (shelter != null) {
                setShelterDisplayValues(shelter)
            }
        }
        detailedViewModel.advertFetchSuccessLiveData.observe(viewLifecycleOwner) { fetchSuccess ->
            if (!fetchSuccess) {
                Snackbar.make(binding.root, R.string.advert_fetch_fail, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
        detailedViewModel.petFetchSuccessLiveData.observe(viewLifecycleOwner) { fetchSuccess ->
            if (!fetchSuccess) {
                Snackbar.make(binding.root, R.string.pet_fetch_fail, Snackbar.LENGTH_SHORT)
                  .show()
            }
        }
        detailedViewModel.shelterFetchSuccessLiveData.observe(viewLifecycleOwner) { fetchSuccess ->
            if (!fetchSuccess) {
                Snackbar.make(binding.root, R.string.shelter_fetch_fail, Snackbar.LENGTH_SHORT)
                  .show()
            }
        }
    }

    private fun setAdvertDisplayValues(advert: Advert) {
        with(binding) {

        }
    }

    private fun setPetDisplayValues(pet: Pet) {
        with(binding) {
            petSpeciesValue.text = pet.species
            petNameValue.text = pet.name
            petColorValue.text = pet.color
            petAgeValue.text = pet.age
            petDescriptionValue.text = pet.description

        }
    }

    private fun setShelterDisplayValues(shelter: Shelter) {
        with(binding) {
            shelterValue.text = shelter.name
        }
    }

    private fun displayComments() {
        with(binding) {
            adapter = CommentsAdapter(emptyList())

            detailedViewModel.commentsLiveData.observe(viewLifecycleOwner) { comments ->
                if (!comments.isNullOrEmpty()) {
                    adapter.updateData(comments)
                    if (noCommentsMessage.isVisible) {
                        commentsPresentDisplay.isVisible = true
                        noCommentsMessage.isVisible = false
                    }
                } else {
                    commentsPresentDisplay.isVisible = false
                    noCommentsMessage.isVisible = true
                }
            }
            detailedViewModel.commentAddedLiveData.observe(viewLifecycleOwner) { reviewAdded ->
                if (!reviewAdded) {
                    Snackbar.make(binding.root, R.string.comment_post_fail, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
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