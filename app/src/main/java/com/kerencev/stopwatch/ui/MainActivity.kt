package com.kerencev.stopwatch.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        viewModel.firstData
            .onEach {
                binding.textTime.text = it
            }
            .launchIn(scope)

        viewModel.secondData
            .onEach {
                binding.textTime2.text = it
            }
            .launchIn(scope)
    }

    private fun setButtonsClicks() {
        with(binding) {
            buttonStart.setOnClickListener { viewModel.startFirstStopwatch() }
            buttonPause.setOnClickListener { viewModel.pauseFirstStopwatch() }
            buttonStop.setOnClickListener { viewModel.stopFirstStopwatch() }

            buttonStart2.setOnClickListener { viewModel.startSecondStopwatch() }
            buttonPause2.setOnClickListener { viewModel.pauseSecondStopwatch() }
            buttonStop2.setOnClickListener { viewModel.stopSecondStopwatch() }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}