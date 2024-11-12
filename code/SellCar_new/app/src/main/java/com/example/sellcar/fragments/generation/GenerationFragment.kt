package com.example.sellcar.fragments.generation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sellcar.data.Auto
import com.example.sellcar.data.Generation
import com.example.sellcar.databinding.FragmentFavoriteBinding
import com.example.sellcar.databinding.FragmentGenerationBinding
import com.example.sellcar.fragments.favorite.FavoriteFragment
import com.example.sellcar.fragments.models.ModelFragmentArgs
import com.example.sellcar.fragments.models.ModelFragmentDirections


class GenerationFragment : Fragment() {

    private lateinit var binding : FragmentGenerationBinding
    private val generations = mapOf(
        "Audi" to mapOf(
            "A4" to listOf(
                Generation("B7", 2004, 2009),
                Generation("B8", 2011, 2015),
                Generation("B5", 1999, 2000)
            ),
            "Q5" to listOf(
                Generation("8R", 2008, 2012),
                Generation("FY", 2016, 2020)
            ),
            "A6" to listOf(
                Generation("C6", 2004, 2008),
                Generation("C5", 1997, 2001)
            )
        ),
        "Toyota" to mapOf(
            "Camry" to listOf(
                Generation("XV70", 2020, 2024),
                Generation("XV55", 2014, 2017),
                Generation("XV30", 2004, 2006)
            ),
            "Corolla" to listOf(
                Generation("E140", 2006, 2010),
                Generation("E210", 2018, 2023)
            ),
            "RAV4" to listOf(
                Generation("XA30", 2005, 2009),
                Generation("XA40", 2013, 2015),
            )
        ),
        "Volkswagen" to mapOf(
            "Golf" to listOf(
                Generation("I", 1974, 1993),
                Generation("IV", 1997, 2006),
                Generation("VIII", 2019, 2023)
            ),
            "Passat" to listOf(
                Generation("B5", 2000, 2005),
                Generation("B8", 2014, 2019)
            ),
            "Tiguan" to listOf(
                Generation("I", 2007, 2011),
                Generation("II", 2016, 2021)
            )
        ),
        "Volvo" to mapOf(
            "S60" to listOf(
                Generation("S60 I", 2000, 2004),
                Generation("II", 2010, 2013),
                Generation("III", 2019, 2023)
            ),
            "XC60" to listOf(
                Generation("I", 2008, 2013),
                Generation("II", 2017, 2022)
            ),
            "V60" to listOf(
                Generation("I", 2010, 2013),
            )
        )
    )
    private lateinit var auto : Auto

    companion object {
        fun newInstance() = GenerationFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGenerationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            auto = GenerationFragmentArgs.fromBundle(it).auto
        }
        val listOfYears = getYearsForModel(auto.brand, auto.model)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listOfYears)
        binding.yearList.adapter = adapter
        binding.yearList.setOnItemClickListener { parent, _, position, _ ->
            val year  = parent.getItemAtPosition(position).toString().toInt()
            auto.year = year
            auto.generation = getGenerationNameForYear(auto.brand, auto.model, auto.year)
            val action = GenerationFragmentDirections.actionNavigationGenerationToBodyCarFragment(auto)
            findNavController().navigate(action)
        }
    }

    private fun getYearsForModel(brand: String, model: String): List<Int> {
        val modelGenerations = generations[brand]?.get(model)
        val years = mutableListOf<Int>()

        if (modelGenerations != null) {
            for (generation in modelGenerations) {
                for (year in generation.yearMin..generation.yearMax) {
                    years.add(year)
                }
            }
        }
        return years.sortedDescending()
    }


    private fun getGenerationNameForYear(brand: String, model: String, year: Int): String {
        val modelGenerations = generations[brand]?.get(model)

        if (modelGenerations != null) {
            for (generation in modelGenerations) {
                if (year in generation.yearMin..generation.yearMax) {
                    return generation.name
                }
            }
        }
        return ""
    }
}