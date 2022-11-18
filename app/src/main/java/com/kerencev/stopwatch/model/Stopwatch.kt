package com.kerencev.stopwatch.model

import com.kerencev.stopwatch.ui.StopwatchStateHolder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface Stopwatch {

    val ticker: Flow<String>

    fun start()
    fun pause()
    fun stop()

    class StopwatchListOrchestrator(
        private val stopwatchStateHolder: StopwatchStateHolder,
    ) : Stopwatch {

        override val ticker = MutableStateFlow("00:00:000")
        private var job: Job? = null
        private val scope = CoroutineScope(Dispatchers.IO)

        override fun start() {
            if (job == null) startJob()
            stopwatchStateHolder.start()
        }

        override fun pause() {
            stopwatchStateHolder.pause()
            stopJob()
        }

        override fun stop() {
            stopwatchStateHolder.stop()
            stopJob()
            clearValue()
        }

        private fun startJob() {
            scope.launch {
                while (isActive) {
                    ticker.value = stopwatchStateHolder.getStringTimeRepresentation()
                    delay(20)
                }
            }
        }

        private fun stopJob() {
            scope.coroutineContext.cancelChildren()
            job = null
        }

        private fun clearValue() {
            ticker.value = "00:00:000"
        }
    }
}