package com.example.sellcar.fragments.brand

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sellcar.data.Auto
import com.example.sellcar.databinding.FragmentBrandBinding
import com.example.sellcar.fragments.models.ModelFragmentArgs


class BrandFragment : Fragment() {

  private lateinit var binding : FragmentBrandBinding
  private lateinit var auto : Auto
  private val brands = listOf(
        "Audi",
        "Toyota",
        "Volkswagen",
        "Volvo"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBrandBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            auto = BrandFragmentArgs.fromBundle(it).auto
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, brands)
        binding.brandList.adapter = adapter
        binding.brandList.setOnItemClickListener { parent, _, position, _ ->
            val brand  = parent.getItemAtPosition(position).toString()
            auto.brand = brand
            val action = BrandFragmentDirections.actionNavigationBrandToNavigationModel(auto)
            findNavController().navigate(action)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BrandFragment()
    }
}