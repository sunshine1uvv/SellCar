package com.example.sellcar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sellcar.data.Auto
import com.example.sellcar.databinding.FragmentBodyCarBinding
import com.example.sellcar.databinding.FragmentTransmissionBinding
import com.example.sellcar.fragments.brand.BrandFragmentDirections
import com.example.sellcar.fragments.models.ModelFragmentArgs


class TransmissionFragment : Fragment() {

    private lateinit var binding : FragmentTransmissionBinding
    private val transmissions  = listOf("Механика", "Автомат")
    private lateinit var auto: Auto

    companion object {
        fun newInstance() = TransmissionFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransmissionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            auto = TransmissionFragmentArgs.fromBundle(it).auto
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, transmissions)
        binding.transmissionList.adapter = adapter
        binding.transmissionList.setOnItemClickListener { parent, _, position, _ ->
            val transmission  = parent.getItemAtPosition(position).toString()
            auto.transmission = transmission
            val action = TransmissionFragmentDirections.actionTransmissionFragmentToStateFragment(auto)
            findNavController().navigate(action)
        }

    }
}