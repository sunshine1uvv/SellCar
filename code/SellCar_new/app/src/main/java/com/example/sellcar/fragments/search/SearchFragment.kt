package com.example.sellcar.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sellcar.adapters.AutoAdapter
import com.example.sellcar.data.Auto
import com.example.sellcar.databinding.FragmentSearchBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var autoList: ArrayList<Auto>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var rvAdapter: AutoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        databaseRef = FirebaseDatabase.getInstance().getReference("auto")
        autoList = arrayListOf()
        fetchData()
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
        }
        return binding.root
    }

    private fun fetchData() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                autoList.clear()
                if (snapshot.exists()) {
                    for (autoSnap in snapshot.children) {
                        val auto = autoSnap.getValue(Auto::class.java)
                        auto?.let { autoList.add(it) }
                    }
                }
                rvAdapter = AutoAdapter(autoList) { autoId ->
                    addToFavorites(autoId)
                }
                binding.recyclerView.adapter = rvAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Ошибка получения данных", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addToFavorites(autoId: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
            userRef.child("favorite").child(autoId).setValue(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Ошибка добавления в избранное", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show()
        }
    }
}