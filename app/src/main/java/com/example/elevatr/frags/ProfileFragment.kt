package com.example.elevatr.frags

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.elevatr.R
import com.example.elevatr.databinding.FragmentProfileBinding
import com.example.elevatr.loginActivity
import com.example.elevatr.res.ResponseList
import com.example.elevatr.res.ResponseListItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlin.collections.isNullOrEmpty


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val logoutButton = view.findViewById<Button>(R.id.buttonSignOut)


        logoutButton.setOnClickListener {
           askatoleave()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
    }

    private fun askatoleave() {
        val builder= AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setIcon(R.drawable.ic_logout)
        builder.setPositiveButton("YES",DialogInterface.OnClickListener { dialog, which ->

            FirebaseAuth.getInstance().signOut()

            Toast.makeText(requireContext(), "Logout successfully!!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), loginActivity::class.java))

            requireActivity().finish() // Close all activities and exit the app

        })
        builder.setNegativeButton("CANCEL",DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.show()


    }

    fun fetchData() {
        val user = FirebaseAuth.getInstance().currentUser ?: run {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance(
            "https://elevatr-01-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )

        val ref = database.getReference("linkedin").child(user.uid)

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val t = object : GenericTypeIndicator<ArrayList<ResponseListItem>>() {}
                    val data = snapshot.getValue(t)
                    if (!data.isNullOrEmpty()) {
                        val profile = data.first()
                        binding.textUserName.text = profile.fullName
                        binding.textUserEmail.text = user.email
                        binding.textFullName.text = profile.fullName
                        binding.textEmail.text = user.email
                        binding.textLoc.text = profile.location
                        Log.d("ProfileFragment", "Data fetched successfully: $profile")
                    }
                } else {
                    Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }




}