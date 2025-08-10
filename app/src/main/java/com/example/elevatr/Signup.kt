package com.example.elevatr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.elevatr.databinding.ActivitySignupBinding
import com.example.elevatr.res.ResponseList
import com.example.elevatr.res.ResponseListItem
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Signup : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textLogin.setOnClickListener {
            intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
        }

        binding.buttonSignIn.setOnClickListener {
            FirebaseDatabase.getInstance(
                "https://elevatr-01-default-rtdb.asia-southeast1.firebasedatabase.app/"
            ).getReference("lve").child("test").setValue("test").addOnSuccessListener {
                Log.d("Firebase", "Data written successfully")
            }.addOnFailureListener { error ->
                Log.e("Firebase", "Failed to write data: ${error.message}")
            }
            val email = binding.editTextEmail.text.toString()
            val pass = binding.editTextPassword.text.toString()
            val cnf_pass = binding.editTextCnfPassword.text.toString()
            val linkedin = binding.editTextLinkedin.text.toString()

            if (email.isEmpty()) binding.editTextEmail.error = "Enter your Email"
            if (pass.isEmpty()) Toast.makeText(this,"Enter the password",Toast.LENGTH_SHORT).show()
            if (pass.length < 6) {
                binding.editTextPassword.error = "Password must be at least 6 characters"
            }
            if (linkedin.isEmpty()) binding.editTextLinkedin.error = "Enter your LinkedIn URL"
            if (cnf_pass.isEmpty()) Snackbar.make(
                binding.main,
                "Re-enter the password",
                Snackbar.LENGTH_SHORT
            ).show()
            else {

                if (pass == cnf_pass) {

                    binding.progressBar.visibility = View.VISIBLE
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {

                            lifecycleScope.launch {
                                val jsonString = fetchLinkedInData(extractLinkId(linkedin))
                                if (jsonString != null) {
                                    val gson = Gson()
                                    val listType = object : TypeToken<List<ResponseListItem>>() {}.type
                                    val profiles: List<ResponseListItem> = gson.fromJson(jsonString, listType)
                                    saveRawJsonToFirebase(profiles)


                                }else{
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(this@Signup, "Failed to fetch LinkedIn data", Toast.LENGTH_SHORT).show()
                                }

                            }


                        } else {

                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {

                    Snackbar.make(binding.main, "Password is not matching", Snackbar.LENGTH_SHORT)
                        .show()

                }

            }

        }
    }

    suspend fun fetchLinkedInData(linkId: String): String? = withContext(Dispatchers.IO) {
        try {
            Log.d("LinkedIn", "Fetching data for linkId: $linkId")
            val apiUrl = "https://api.scrapingdog.com/linkedin/?api_key=$API_KEY&type=profile&linkId=$linkId&premium=true"
            Log.d("LinkedIn", "API URL: $apiUrl")
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            return@withContext if (connection.responseCode == 200) {
                Log.d("LinkedIn", "Response code: ${connection.responseCode}")
                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    response.toString()
                }
            } else {
                Log.e("LinkedIn", "Failed to fetch data: ${connection.responseCode}")
                Toast.makeText(this@Signup, "Failed to fetch LinkedIn data", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                null
            }
        } catch (e: Exception) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this@Signup, "Error fetching LinkedIn data: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("LinkedIn", "Error fetching data", e)
            e.printStackTrace()
            null
        }
    }

    fun saveRawJsonToFirebase(json: List<ResponseListItem>) {
        Log.d("LinkedIn", "Saving JSON to Firebase: $json")
        val uid = firebaseAuth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance(
            "https://elevatr-01-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("linkedin").child(uid)

        database.setValue(json)
            .addOnSuccessListener {
                Log.d("LinkedIn", "Data saved successfully")
                binding.progressBar.visibility = View.GONE
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Log.d("LinkedIn", "Failed to save data: ${error.message}")
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }




    fun extractLinkId(link: String): String {
        // 1. Remove any query parameters (everything after '?')
        val cleanUrl = link.substringBefore("?")

        // 2. Remove trailing slashes if any
        val trimmedUrl = cleanUrl.trimEnd('/')

        // 3. Take the last segment after '/'
        return trimmedUrl.substringAfterLast("/")
    }


}