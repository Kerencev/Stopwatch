package com.kerencev.stopwatch.ui

import androidx.lifecycle.ViewModel
import com.kerencev.stopwatch.model.Stopwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseViewModel : ViewModel() {

    abstract val data: Flow<String>

    abstract fun start()
    abstract fun pause()
    abstract fun stop()

    class MainViewModel : BaseViewModel() {

        override val data = MutableStateFlow("")
        private val scope = CoroutineScope(Dispatchers.IO)

        private val timestampProvider = object : TimestampProvider {
            override fun getMilliseconds(): Long {
                return System.currentTimeMillis()
            }
        }

        private val stopwatchListOrchestrator = Stopwatch.StopwatchListOrchestrator(
            StopwatchStateHolder(
                StopwatchStateCalculator(
                    timestampProvider,
                    ElapsedTimeCalculator(timestampProvider)
                ),
                ElapsedTimeCalculator(timestampProvider),
                TimestampMillisecondsFormatter()
            )
        )

        init {
            stopwatchListOrchestrator.ticker
                .onEach {
                    data.value = it
                }
                .launchIn(scope)
        }

        override fun start() {
            stopwatchListOrchestrator.start()
        }

        override fun pause() {
            stopwatchListOrchestrator.pause()
        }

        override fun stop() {
            stopwatchListOrchestrator.stop()
        }
    }
}