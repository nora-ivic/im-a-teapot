package progi.imateacup.nestaliljubimci.ui.search

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import progi.imateacup.nestaliljubimci.databinding.FragmentSearchBinding
import progi.imateacup.nestaliljubimci.model.networking.entities.SearchFilter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class SearchFragment : Fragment() {

    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: FragmentSearchBinding? = null
    private var age: Int? = null
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
        with(binding) {
            topAppBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            searchButton.setOnClickListener {
                val navController = findNavController()
                navController.previousBackStackEntry?.savedStateHandle?.set("filter", Json.encodeToString(fillFilter()))
                navController.popBackStack()
            }
            calendarIcon.setOnClickListener {
                datePicker.show(parentFragmentManager, "datePicker")
            }
            datePicker.addOnPositiveButtonClickListener {
                chosenDatumDisplay.text = datePicker.headerText
            }
            starostSlider.addOnChangeListener { _, value, _ ->
                age = value.toInt()
                if (age != ageTextField.text.toString().toInt()) {
                    ageTextField.setText(age.toString())
                }
            }
            ageTextField.addTextChangedListener(onTextChanged = { text, _, _, _ ->
                val textInput = text.toString()
                if (textInput.isNotEmpty() && textInput.toInt() != age) { //check against age to guard against infinite loop and cyclic dependency
                    if (textInput.toInt() > 75) {
                        age = 75
                        ageTextField.setText(75.toString())
                    } else {
                        age = textInput.toInt()
                        starostSlider.setValues(text.toString().toFloat())
                    }
                }
            })
        }
    }

    private fun fillFilter(): SearchFilter {
        val inputFormat = SimpleDateFormat("MMM dd, yyyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


        with(binding) {
            var date: Date?
            var formattedDate: String? = null
            try {
                date = inputFormat.parse(chosenDatumDisplay.text.toString())
                formattedDate = outputFormat.format(date!!)
            }
            catch (e: ParseException) {
                date = null
            }

            println(formattedDate)

            return SearchFilter(
                ime = textInputEditTextIme.text.toString(),
                vrsta = autocompleteTextVrsta.text.toString(),
                boja = autocompleteTextBoja.text.toString(),
                starost = age.toString(),
                datumNestanka = formattedDate,
                description = textInputEditTextOpis.text.toString(),
            )
        }
    }
}

/**TODO
 * IMPROVEMENT: - Dodat kruzic sa bojom pokraj odabira boje
 *              - Pomaknut pretrazi gumb u bottom app bar
 *
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