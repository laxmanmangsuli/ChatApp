package com.example.chatapp.presentationlayer.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapps.R
import com.example.chatapps.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenImageBinding
    private var currentPosition = RecyclerView.NO_POSITION
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageUri = intent.getStringExtra("imageUri")
        currentPosition = intent.getIntExtra("position", RecyclerView.NO_POSITION)

        Glide.with(this)
            .load(imageUri)
            .into(binding.ivFullscreenImage)

        binding.ivFullscreenImage.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("position", currentPosition)
        setResult(Activity.RESULT_OK, resultIntent)
        super.onBackPressed()
    }
}
