package com.example.sellcar.fragments.other

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sellcar.R
import com.example.sellcar.data.User
import com.example.sellcar.data.models.UserViewModel
import com.example.sellcar.databinding.FragmentOtherBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OtherFragment : Fragment() {

    private lateinit var binding: FragmentOtherBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentOtherBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        database = FirebaseDatabase.getInstance().reference
        checkUserAuthentication()
    }

    private fun checkUserAuthentication() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            fetchUserData(user.uid)
        } else {
            updateUI(null)
        }
    }

    private fun fetchUserData(userId: String) {
        database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userMap: Map<String, Any>? = snapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})

                userMap?.let {
                    val username = it["username"] as? String ?: ""
                    val email = it["email"] as? String ?: ""
                    val adsMap = it["ads"] as? Map<String, Boolean> ?: emptyMap()
                    val adsList = adsMap.keys.toList()
                    val user = User(username, email, adsList)

                    updateUI(user)
                } ?: run {
                    updateUI(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Ошибка получения данных: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(user: User?) {
        user?.let {
            binding.tvUserName.text = "Имя пользователя: ${it.username}"
            binding.tvUserEmail.text = "Email: ${it.email}"
            binding.layoutUserInfo.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.GONE
            binding.btnLogout.visibility = View.VISIBLE
            binding.btnMyAds.visibility = View.VISIBLE
        } ?: run {
            binding.layoutUserInfo.visibility = View.GONE
            binding.tvUserName.text = ""
            binding.tvUserEmail.text = ""
            binding.btnLogin.visibility = View.VISIBLE
            binding.btnLogout.visibility = View.GONE
            binding.btnMyAds.visibility = View.GONE
        }
    }

    private fun setListeners() {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_other_to_loginFragment)
        }

        binding.btnLogout.setOnClickListener {
            signOut()
        }
        binding.btnMyAds.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_other_to_myAdsFragment)
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        userViewModel.clearUser()
        updateUI(null)
    }
}