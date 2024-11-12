package com.example.sellcar.fragments.models

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sellcar.data.Auto
import com.example.sellcar.databinding.FragmentModelBinding


class ModelFragment : Fragment() {

    private lateinit var binding: FragmentModelBinding
    private lateinit var auto: Auto
    private val carModels = mapOf(
        "Audi" to listOf("A4", "Q5", "A6"),
        "Toyota" to listOf("Camry", "Corolla", "RAV4"),
        "Volkswagen" to listOf("Golf", "Passat", "Tiguan"),
        "Volvo" to listOf("S60", "XC60", "V60")
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModelBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            auto = ModelFragmentArgs.fromBundle(it).auto
        }
        val models = getModelsByBrand(auto.brand)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, models)
        binding.modelList.adapter = adapter
        binding.modelList.setOnItemClickListener { parent, _, position, _ ->
            val model  = parent.getItemAtPosition(position).toString()
            auto.model = model
            val action = ModelFragmentDirections.actionNavigationModelToNavigationGeneration(auto)
            findNavController().navigate(action)
        }
    }

    private  fun getModelsByBrand(brand: String): List<String> {
        return carModels[brand]!!
    }
//    fun getModelsByBrand(brand: String, callback: (List<String>) -> Unit) {
//        val dbRef = FirebaseDatabase.getInstance().reference.child("cars").child(brand)
//
//        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val models = snapshot.children.mapNotNull { it.key }
//                callback(models)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                callback(emptyList())
//            }
//        })
//    }

    companion object {
        @JvmStatic
        fun newInstance() = ModelFragment()
    }
}