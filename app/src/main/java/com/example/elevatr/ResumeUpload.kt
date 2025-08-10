package com.example.elevatr

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.InputStream

class ResumeUpload : AppCompatActivity() {
    private lateinit var binding: com.example.elevatr.databinding.ActivityResumeUploadBinding
    private val pickPdfLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.progressBar.visibility = View.VISIBLE
                extractPdfText(it)
            } ?: run {
                Toast.makeText(this@ResumeUpload, "No PDF selected", Toast.LENGTH_SHORT).show()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = com.example.elevatr.databinding.ActivityResumeUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener {
            // Close the current activity and go back
            onBackPressedDispatcher.onBackPressed()
        }

        binding.buttonBrowseFiles.setOnClickListener {
            // Open file picker to select PDF
            pickPdfLauncher.launch("application/pdf")
        }

        binding.buttonAnalyze.setOnClickListener {
            // Check if text is already extracted
            if (binding.extText.text.isNotEmpty()&&binding.editTextJobDescription.text?.isNotEmpty()!!) {
                // Proceed with analysis
                Toast.makeText(this, "Analyzing resume...", Toast.LENGTH_SHORT).show()
                // Here you can add your analysis logic
            } else {
                Toast.makeText(this, "Please select a PDF and give job description", Toast.LENGTH_SHORT).show()
            }
        }



    }

    private fun extractPdfText(uri: Uri) {
        // Show basic info
        var displayName = "Unknown"
        var fileSize = "Unknown"

        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            displayName = cursor.getString(nameIndex)
            fileSize = cursor.getLong(sizeIndex).toString() + " bytes"
        }

        Toast.makeText(this,"File: $displayName\nSize: $fileSize", Toast.LENGTH_LONG).show()

        // Extract text
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()
            val text = stripper.getText(document)
            document.close()
            binding.progressBar.visibility = View.GONE
            binding.extText.text = text.ifBlank { "No text found in this PDF ❗" }
        } catch (e: Exception) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Error reading PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            binding.extText.text = "Error reading PDF: ${e.message}"
        }
    }
}