package com.example.elevatr.frags

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elevatr.R
import com.example.elevatr.ResumeUpload
import com.example.elevatr.myAdapter


class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val strBtn = view.findViewById<Button>(R.id.buttonStart)
        strBtn.setOnClickListener {
            val intent = Intent(requireContext(), ResumeUpload::class.java)
            startActivity(intent)
        }

        val recycle = view.findViewById<RecyclerView>(R.id.recyclerViewHistory)
        recycle.layoutManager = LinearLayoutManager(requireContext())
        recycle.adapter = myAdapter(
            listOf(
                "Resume 1",
                "Resume 2"
            )
        )



        return view
    }


}