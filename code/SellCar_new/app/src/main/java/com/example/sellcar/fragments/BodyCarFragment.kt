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


class BodyCarFragment : Fragment() {

    private lateinit var binding : FragmentBodyCarBinding
    private val carBodies = listOf("Седан", "Универсал")
    private lateinit var auto: Auto

    companion object {
        fun newInstance() = BodyCarFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBodyCarBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            auto = BodyCarFragmentArgs.fromBundle(it).auto
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, carBodies)
        binding.carBodiesList.adapter = adapter
        binding.carBodiesList.setOnItemClickListener { parent, _, position, _ ->
            val carBody  = parent.getItemAtPosition(position).toString()
            auto.carBody = carBody
            val action = BodyCarFragmentDirections.actionBodyCarFragmentToTransmissionFragment(auto)
            findNavController().navigate(action)
        }
    }
}