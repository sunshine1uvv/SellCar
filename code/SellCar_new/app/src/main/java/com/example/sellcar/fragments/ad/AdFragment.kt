package com.example.sellcar.fragments.ad

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sellcar.adapters.PhotoAdapter
import com.example.sellcar.data.Auto
import com.example.sellcar.data.User
import com.example.sellcar.databinding.FragmentAdBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class AdFragment : Fragment() {

    private lateinit var binding: FragmentAdBinding
    private var auto: Auto? = null
    private var selectedImages: List<Uri> = emptyList()
    private lateinit var photoAdapter: PhotoAdapter
    private var databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("auto")
    private var storageRef: StorageReference = FirebaseStorage.getInstance().getReference("images")

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == -1) {
            val data = result.data
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                selectedImages = List(count) { data.clipData!!.getItemAt(it).uri }
            } else {
                data?.data?.let { uri -> selectedImages = listOf(uri) }
            }
            setupViewPager()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auto = arguments?.let { AdFragmentArgs.fromBundle(it).auto } ?: Auto()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            checkUserExists(userId) { exists ->
                if (exists) {
                    setupUI()
                } else {
                    showLoginPrompt()
                }
            }
        } else {
            showLoginPrompt()
        }
    }


    private fun checkUserExists(userId: String, onResult: (Boolean) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            onResult(snapshot.exists())
        }.addOnFailureListener {
            onResult(false)
        }
    }

    private fun setupUI() {
        updateUI()
        setupListeners()
        binding.btnSelectPhoto.visibility = View.VISIBLE
        binding.btnSaveData.visibility = View.VISIBLE
        binding.button.visibility = View.VISIBLE
        binding.tvLoginPrompt.visibility = View.GONE
        photoAdapter = PhotoAdapter(selectedImages)
        binding.viewPager.adapter = photoAdapter
    }

    private fun showLoginPrompt() {
        binding.btnSelectPhoto.visibility = View.GONE
        binding.btnSaveData.visibility = View.GONE
        binding.tvParams.visibility = View.GONE
        binding.viewPager.visibility = View.GONE
        binding.tvPhotos.visibility = View.GONE
        binding.tvPrice.visibility = View.GONE
        binding.tvCarBody.visibility = View.GONE
        binding.tvBrand.visibility = View.GONE
        binding.tvModel.visibility = View.GONE
        binding.tvGeneration.visibility = View.GONE
        binding.tvTransmission.visibility = View.GONE
        binding.tvYear.visibility = View.GONE
        binding.tvState.visibility = View.GONE
        binding.editTextPrice.visibility = View.GONE
        binding.button.visibility = View.GONE
        binding.tvLoginPrompt.visibility = View.VISIBLE
    }

    private fun setupListeners() {
        binding.button.setOnClickListener {
            val action = AdFragmentDirections.actionNavigationAdToNavigationBrand(auto!!)
            findNavController().navigate(action)
        }

        binding.btnSelectPhoto.setOnClickListener {
            openGallery()
        }

        binding.btnSaveData.setOnClickListener {
            if (!isUserLoggedIn()) {
                Toast.makeText(context, "Пожалуйста, войдите в систему, чтобы добавить объявление", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auto!!.price = binding.editTextPrice.text.toString().toFloat()
            if (auto != null && auto!!.isNotEmpty() && selectedImages.isNotEmpty()) {
                binding.btnSaveData.text = "Загрузка..."
                saveData()
            } else {
                Toast.makeText(context, "Введите параметры автомобиля и выберите фотографии", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        if (auto == null || auto?.isEmpty() == true) {
            binding.tvParams.visibility = View.GONE
            binding.tvBrand.visibility = View.GONE
            binding.tvModel.visibility = View.GONE
            binding.tvGeneration.visibility = View.GONE
            binding.tvYear.visibility = View.GONE
            binding.tvCarBody.visibility = View.GONE
            binding.tvTransmission.visibility = View.GONE
            binding.tvState.visibility = View.GONE
        } else {
            binding.tvParams.visibility = View.VISIBLE
            binding.tvBrand.visibility = View.VISIBLE
            binding.tvModel.visibility = View.VISIBLE
            binding.tvGeneration.visibility = View.VISIBLE
            binding.tvYear.visibility = View.VISIBLE
            binding.tvCarBody.visibility = View.VISIBLE
            binding.tvTransmission.visibility = View.VISIBLE
            binding.tvState.visibility = View.VISIBLE

            binding.tvParams.text = "Параметры:"
            binding.tvBrand.text = "Марка: ${auto?.brand}"
            binding.tvModel.text = "Модель: ${auto?.model}"
            binding.tvGeneration.text = "Поколение: ${auto?.generation}"
            binding.tvYear.text = "Год: ${auto?.year}"
            binding.tvCarBody.text = "Кузов: ${auto?.carBody}"
            binding.tvTransmission.text = "Коробка передач: ${auto?.transmission}"
            binding.tvState.text = "Состояние: ${auto?.state}"
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startForResult.launch(intent)
    }

    private fun setupViewPager() {
        photoAdapter.updatePhotos(selectedImages)
    }

    private fun saveData() {
        val imagesUrl = ArrayList<String>()
        val id = databaseRef.push().key!!
        auto!!.id = id

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(context, "Пожалуйста, войдите в систему, чтобы добавить объявление", Toast.LENGTH_SHORT).show()
            return
        }

        auto!!.userId = userId

        val uploadTasks = selectedImages.map { uri ->
            val storageReference = storageRef.child(id).child(UUID.randomUUID().toString())
            storageReference.putFile(uri).continueWithTask { task ->
                storageReference.downloadUrl
            }.addOnSuccessListener { url ->
                imagesUrl.add(url.toString())
            }
        }

        Tasks.whenAllSuccess<List<Tasks>>(uploadTasks).addOnCompleteListener {
            binding.btnSaveData.text = "Создать объявление"
            auto!!.imageUrls = imagesUrl
            databaseRef.child(auto!!.id).setValue(auto).addOnSuccessListener {
                updateUserAds(userId, auto!!.id)
                Toast.makeText(context, "Объявление успешно создано", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Ошибка при создании объявления", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserAds(userId: String, adId: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userMap = snapshot.value as? HashMap<String, Any>
                val updatedAds = (userMap?.get("ads") as? List<String>)?.toMutableList() ?: mutableListOf()
                updatedAds.add(adId)
                userRef.child("ads").setValue(updatedAds)
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

}

