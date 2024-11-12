package com.example.sellcar.fragments.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sellcar.adapters.AutoAdapter
import com.example.sellcar.data.Auto
import com.example.sellcar.databinding.FragmentFavoriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var autoList: MutableList<Auto>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var rvAdapter: AutoAdapter

    companion object {
        fun newInstance() = FavoriteFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        autoList = mutableListOf()
        rvAdapter = AutoAdapter(autoList) {  }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

        fetchFavorites()
    }

    private fun fetchFavorites() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorite")

            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    autoList.clear()
                    if (snapshot.exists()) {
                        for (favoriteSnap in snapshot.children) {
                            val autoId = favoriteSnap.key
                            autoId?.let { fetchAutoDetails(it) }
                        }
                    } else {
                        Toast.makeText(context, "Нет избранных объявлений", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Ошибка получения данных", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAutoDetails(autoId: String) {
        val autoRef = FirebaseDatabase.getInstance().getReference("auto").child(autoId)
        autoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val auto = snapshot.getValue(Auto::class.java)
                auto?.let {
                    autoList.add(it)
                    rvAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Ошибка получения данных", Toast.LENGTH_SHORT).show()
            }
        })
    }
}