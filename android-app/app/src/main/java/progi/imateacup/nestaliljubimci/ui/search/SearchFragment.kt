package progi.imateacup.nestaliljubimci.ui.search

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import progi.imateacup.nestaliljubimci.R
import progi.imateacup.nestaliljubimci.databinding.FragmentSearchBinding
import progi.imateacup.nestaliljubimci.ui.pets.PetsFragment

class SearchFragment : Fragment() {

    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDatePicker()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        val bundle = Bundle().apply {
            putString("key", "yourData")
        }
        findNavController().popBackStack()
    }

    private fun initDatePicker() {
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Odaberite datum nestanka")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
    }

    private fun initListeners() {
        with(binding) {
            searchButton.setOnClickListener {
                val navController = findNavController()
                navController.previousBackStackEntry?.savedStateHandle?.set("filter", fillFilter())
            }
            calendarIcon.setOnClickListener {
                datePicker.show(parentFragmentManager, "datePicker")
            }
        }
    }

    private fun fillFilter(): Any {

    }
}

/**TODO
 * Kad ces povezivat age slider i text box ne zaboravi stavit ogranicenje za max input na 75 il
 * koliko vec
 */

// Pretrazivanje oglasa po:
//  - Ime
//  - Vrsta
//  - Boja
//  - Starost
//  - Datum nestanka
//  - Description
//  - (opcionalno Lokacija)

//  - Username

//  - Kategorija oglasa

//  - Shelter name

/**
 * Menu
 * Izgubljeni
 * Prondađeni
 * Prekinuto traženje
 * U skloništu
 * Uginuli
 * -------------
 * Search -> submenu: Pretraži ljubimce
 *                    Ljubimci korisnika
 *                    Ljubimci u skloništu
 */