package com.example.elevatr

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.elevatr.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class loginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textSignUp.setOnClickListener {
            intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        binding.buttonSignIn.setOnClickListener {


            val email = binding.editTextEmail.text.toString()
            val pass = binding.editTextPassword.text.toString()

            if (email.isEmpty()) binding.editTextEmail.error= "Email cannot be empty."
            if (pass.isEmpty()) Snackbar.make(binding.main,"Password cannot be empty", Snackbar.LENGTH_SHORT).show()
            else{
                binding.progressBar.visibility = View.VISIBLE
                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {

                    if (it.isSuccessful){

                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this,"login successfully !!!", Toast.LENGTH_SHORT).show()
                        intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }

                }

            }

        }

    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser!=null){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}