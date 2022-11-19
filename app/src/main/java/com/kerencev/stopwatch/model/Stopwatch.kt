package com.kerencev.stopwatch.model

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
                    delay(timeMillis = 20)
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

sealed class StopwatchState {

    data class Paused(
        val elapsedTime: Long
    ) : StopwatchState()

    data class Running(
        val startTime: Long,
        val elapsedTime: Long
    ) : StopwatchState()
}

interface TimestampProvider {
    fun getMilliseconds(): Long

    class Base : TimestampProvider {
        override fun getMilliseconds(): Long {
            return System.currentTimeMillis()
        }
    }
}


interface StopwatchStateCalculator {
    fun calculateRunningState(oldState: StopwatchState): StopwatchState
    fun calculatePausedState(oldState: StopwatchState): StopwatchState

    class Base(
        private val timestampProvider: TimestampProvider,
        private val elapsedTimeCalculator: ElapsedTimeCalculator,
    ) : StopwatchStateCalculator {

        override fun calculateRunningState(oldState: StopwatchState): StopwatchState.Running =
            when (oldState) {
                is StopwatchState.Running -> oldState
                is StopwatchState.Paused -> {
                    StopwatchState.Running(
                        startTime = timestampProvider.getMilliseconds(),
                        elapsedTime = oldState.elapsedTime
                    )
                }
            }

        override fun calculatePausedState(oldState: StopwatchState): StopwatchState.Paused =
            when (oldState) {
                is StopwatchState.Running -> {
                    val elapsedTime = elapsedTimeCalculator.calculate(oldState)
                    StopwatchState.Paused(elapsedTime = elapsedTime)
                }
                is StopwatchState.Paused -> oldState
            }
    }
}


interface ElapsedTimeCalculator {
    fun calculate(state: StopwatchState.Running): Long

    class Base(
        private val timestampProvider: TimestampProvider,
    ) : ElapsedTimeCalculator {

        override fun calculate(state: StopwatchState.Running): Long {
            val currentTimestamp = timestampProvider.getMilliseconds()
            val timePassedSinceStart = if (currentTimestamp > state.startTime) {
                currentTimestamp - state.startTime
            } else {
                0
            }
            return timePassedSinceStart + state.elapsedTime
        }
    }
}

interface TimestampMillisecondsFormatter {
    fun format(timestamp: Long): String

    class Base : TimestampMillisecondsFormatter {

        override fun format(timestamp: Long): String {
            val millisecondsFormatted = (timestamp % 1000).pad(3)
            val seconds = timestamp / 1000
            val secondsFormatted = (seconds % 60).pad(2)
            val minutes = seconds / 60
            val minutesFormatted = (minutes % 60).pad(2)
            val hours = minutes / 60
            return if (hours > 0) {
                val hoursFormatted = (minutes / 60).pad(2)
                "$hoursFormatted:$minutesFormatted:$secondsFormatted"
            } else {
                "$minutesFormatted:$secondsFormatted:$millisecondsFormatted"
            }
        }

        private fun Long.pad(desiredLength: Int) = this.toString().padStart(desiredLength, '0')
    }
}


abstract class StopwatchStateHolder {

    protected var currentState: StopwatchState = StopwatchState.Paused(0)

    abstract fun start()
    abstract fun pause()
    abstract fun stop()
    abstract fun getStringTimeRepresentation(): String

    class Base(
        private val stopwatchStateCalculator: StopwatchStateCalculator,
        private val elapsedTimeCalculator: ElapsedTimeCalculator,
        private val timestampMillisecondsFormatter: TimestampMillisecondsFormatter
    ) : StopwatchStateHolder() {

        override fun start() {
            currentState = stopwatchStateCalculator.calculateRunningState(currentState)
        }

        override fun pause() {
            currentState = stopwatchStateCalculator.calculatePausedState(currentState)
        }

        override fun stop() {
            currentState = StopwatchState.Paused(0)
        }

        override fun getStringTimeRepresentation(): String {
            val elapsedTime = when (val currentState = currentState) {
                is StopwatchState.Paused -> currentState.elapsedTime
                is StopwatchState.Running -> elapsedTimeCalculator.calculate(currentState)
            }
            return timestampMillisecondsFormatter.format(elapsedTime)
        }
    }
}

