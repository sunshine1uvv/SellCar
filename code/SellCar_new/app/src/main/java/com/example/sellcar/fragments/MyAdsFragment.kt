package com.example.sellcar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sellcar.adapters.AutoAdapter
import com.example.sellcar.data.Auto
import com.example.sellcar.databinding.FragmentMyAdsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class MyAdsFragment : Fragment() {

    private lateinit var binding: FragmentMyAdsBinding
    private lateinit var rvAdapter: AutoAdapter
    private var adList: MutableList<Auto> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyAdsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchUserAds()
    }

    private fun setupRecyclerView() {
        rvAdapter = AutoAdapter(adList) { }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }
    }

    private fun fetchUserAds() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val adsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("ads")
            adsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    adList.clear()
                    if (snapshot.exists()) {
                        // Пытаемся получить данные как ArrayList<String>
                        val adIds = snapshot.getValue(object : GenericTypeIndicator<ArrayList<String>>() {})

                        if (adIds != null && adIds.isNotEmpty()) {
                            for (adId in adIds) {
                                fetchAds(adId)
                            }
                        } else {
                            Toast.makeText(context, "У вас нет объявлений", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "У вас нет объявлений", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Ошибка получения данных: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAds(autoId: String) {
        val autoRef = FirebaseDatabase.getInstance().getReference("auto").child(autoId)
        autoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val auto = snapshot.getValue(Auto::class.java)
                auto?.let {
                    adList.add(it)
                    rvAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Ошибка получения данных", Toast.LENGTH_SHORT).show()
            }
        })
    }
}