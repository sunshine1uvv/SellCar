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
import com.example.sellcar.databinding.FragmentStateBinding


class StateFragment : Fragment() {

    private lateinit var binding : FragmentStateBinding
    private val states = listOf("С пробегом", "Аварийный", "На запчасти")
    private lateinit var auto: Auto

    companion object {
        fun newInstance() = StateFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStateBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            auto = StateFragmentArgs.fromBundle(it).auto
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, states)
        binding.stateList.adapter = adapter
        binding.stateList.setOnItemClickListener { parent, _, position, _ ->
            val state  = parent.getItemAtPosition(position).toString()
            auto.state = state
            val action = StateFragmentDirections.actionStateFragmentToNavigationAd(auto)
            findNavController().navigate(action)
        }
    }
}