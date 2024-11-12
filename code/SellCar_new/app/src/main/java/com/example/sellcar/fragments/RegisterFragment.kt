package com.example.sellcar.fragments

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
import com.example.sellcar.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.signUpBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            val username = binding.usernameEt.text.toString()

            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(requireContext(), "Поля не должны быть пустыми", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(email, password, username)
            }
        }
    }

    private fun registerUser(email: String, password: String, username: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(username, email, emptyList())
                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                    FirebaseDatabase.getInstance().reference.child("users").child(userId)
                        .setValue(user).addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                userViewModel.setUser(user)
                                findNavController().navigate(R.id.action_registerFragment_to_navigation_other)
                            } else {
                                Toast.makeText(requireContext(), "Ошибка сохранения данных", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}