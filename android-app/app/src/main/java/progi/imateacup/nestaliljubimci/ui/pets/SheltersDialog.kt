package progi.imateacup.nestaliljubimci.ui.pets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import progi.imateacup.nestaliljubimci.databinding.SheltersDialogBinding
import progi.imateacup.nestaliljubimci.model.networking.entities.SearchFilter
import progi.imateacup.nestaliljubimci.model.networking.enums.DisplayState
import progi.imateacup.nestaliljubimci.ui.authentication.PREFERENCES_NAME
import progi.imateacup.nestaliljubimci.util.isInternetAvailable

class SheltersDialog : DialogFragment() {

    private val viewModelShelters by viewModels<SheltersViewModel>()
    private val viewModelPets by viewModels<PetsViewModel>()
    private var _binding: SheltersDialogBinding? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: SheltersAdapter
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.ThemeOverlay_Material_ActionBar)
        sharedPreferences =
            requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = SheltersDialogBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBarPets.setNavigationOnClickListener {
            dismiss()
        }

        adapter = SheltersAdapter(emptyList())
        binding.sheltersRecyclerView.adapter = adapter
        setObservers()
        addScrollPagination()
    }

    private fun addScrollPagination() {
        val layoutManager = LinearLayoutManager(context)
        binding.sheltersRecyclerView.layoutManager = layoutManager
        binding.sheltersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                println("SCROLL TRIGGERED")
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    if (isInternetAvailable(requireContext())) {
                        viewModelShelters.getShelters(
                            isInternetAvailable(requireContext())
                        )
                    }
                }
            }
        })
    }

    private fun setObservers() {
        with(viewModelShelters) {
            displayStateLiveData.observe(viewLifecycleOwner) { state ->
                when (state) {
                    DisplayState.LOADING -> {
                        showLoading()
                    }
                    DisplayState.ERRORGET -> {
                        showError()
                    }
                    DisplayState.SUCCESSGET -> {
                        showShelters()
                    }
                    DisplayState.NOPOSTS -> {
                        showNoPosts()
                    }
                    else -> {
                        showNoPosts()
                    }
                }
            }
            viewModelShelters.sheltersLiveData.observe(viewLifecycleOwner) { shelters ->
                adapter.setShelters(shelters)
            }
        }

    }

    fun showLoading() {
        with (binding) {
            sheltersLoadingProgressBar.visibility = View.VISIBLE
            noPostsDisplay.visibility = View.GONE
            sheltersRecyclerView.visibility = View.GONE
        }
    }
    fun showShelters() {
        with (binding) {
            sheltersLoadingProgressBar.visibility = View.GONE
            noPostsDisplay.visibility = View.GONE
            sheltersRecyclerView.visibility = View.VISIBLE
        }
    }
    fun showError() {
        with (binding) {
            sheltersLoadingProgressBar.visibility = View.GONE
            noPostsDisplay.visibility = View.VISIBLE
            sheltersRecyclerView.visibility = View.GONE
            Snackbar.make(
                binding.root,
                "Error loading shelters",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
    fun showNoPosts() {
        with (binding) {
            sheltersLoadingProgressBar.visibility = View.GONE
            noPostsDisplay.visibility = View.VISIBLE
            sheltersRecyclerView.visibility = View.GONE
        }
    }
}
