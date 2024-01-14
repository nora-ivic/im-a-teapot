package progi.imateacup.nestaliljubimci.ui.pets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import progi.imateacup.nestaliljubimci.databinding.SheltersDialogBinding
import progi.imateacup.nestaliljubimci.model.networking.entities.SearchFilter
import progi.imateacup.nestaliljubimci.util.isInternetAvailable

class SheltersDialog() : DialogFragment() {

    private val viewModelShelters by viewModels<SheltersViewModel>()
    private val viewModelPets by viewModels<PetsViewModel>()
    private var _binding: SheltersDialogBinding? = null
    private lateinit var adapter: SheltersAdapter
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.ThemeOverlay_Material_ActionBar);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SheltersDialogBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelShelters.getShelters()

        adapter = SheltersAdapter(emptyList()) { shelter ->
            viewModelPets.getPets(
                isInternetAvailable(requireContext()),
                SearchFilter(imeSklonista = shelter.name)
            )
            dismiss()
        }
        binding.sheltersRecyclerView.adapter = adapter
        setObservers()
    }

    private fun setObservers() {
        viewModelShelters.sheltersLiveData.observe(viewLifecycleOwner) { shelters ->
            adapter.setShelters(shelters)
        }
    }
}
