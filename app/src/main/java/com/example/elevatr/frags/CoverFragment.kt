package com.example.elevatr.frags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.example.elevatr.R


class CoverFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_cover, container, false)


        val card1 = view.findViewById<CardView>(R.id.cardTemplate1)
        val card2 = view.findViewById<CardView>(R.id.cardTemplate2)
        val card3 = view.findViewById<CardView>(R.id.cardTemplate3)
        val card4 = view.findViewById<CardView>(R.id.cardTemplate4)

        card1.setOnClickListener {
            askForDownload(1)
        }

        card2.setOnClickListener {
            askForDownload(2)
        }
        card3.setOnClickListener {
            askForDownload(3)
        }
        card4.setOnClickListener {
            askForDownload(4)
        }
        return view

    }

    fun askForDownload(type:Int){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Download Resume")
        builder.setMessage("Do you want to download your resume as a PDF?")
        builder.setIcon(R.drawable.ic_download)

        builder.setPositiveButton("YES") { dialog, _ ->

            // Call your download function here
            downloadResumePdf(type)

            Toast.makeText(requireContext(), "Downloading resume...", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()

    }

    fun downloadResumePdf(type: Int) {
        // Implement your PDF download logic here
        // This is a placeholder function
        Toast.makeText(requireContext(), "Downloading resume type $type...", Toast.LENGTH_SHORT).show()
    }


}