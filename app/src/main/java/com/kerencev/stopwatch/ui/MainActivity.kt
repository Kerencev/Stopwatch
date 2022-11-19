package com.kerencev.stopwatch.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kerencev.stopwatch.R
import com.kerencev.stopwatch.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: BaseViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeData()
        setButtonsClicks()
    }

    private fun observeData() {
        viewModel.data
            .onEach {
                binding.textTime.text = it
            }
            .launchIn(scope)
    }

    private fun setButtonsClicks() {
        with(binding) {
            buttonStart.setOnClickListener { viewModel.start() }
            buttonPause.setOnClickListener { viewModel.pause() }
            buttonStop.setOnClickListener { viewModel.stop() }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}