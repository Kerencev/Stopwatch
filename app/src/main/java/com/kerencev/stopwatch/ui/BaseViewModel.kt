package com.kerencev.stopwatch.ui

import androidx.lifecycle.ViewModel
import com.kerencev.stopwatch.model.Stopwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseViewModel : ViewModel() {

    abstract val firstData: Flow<String>
    abstract val secondData: Flow<String>
    protected abstract val scope: CoroutineScope

    abstract fun startFirstStopwatch()
    abstract fun pauseFirstStopwatch()
    abstract fun stopFirstStopwatch()
    abstract fun startSecondStopwatch()
    abstract fun pauseSecondStopwatch()
    abstract fun stopSecondStopwatch()

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }

    class MainViewModel(
        private val firstStopwatch: Stopwatch,
        private val secondStopwatch: Stopwatch,
    ) : BaseViewModel() {

        override val firstData = MutableStateFlow("")
        override val secondData = MutableStateFlow("")
        override val scope = CoroutineScope(Dispatchers.IO)

        init {
            firstStopwatch.ticker
                .onEach {
                    firstData.value = it
                }
                .launchIn(scope)
            secondStopwatch.ticker
                .onEach {
                    secondData.value = it
                }
                .launchIn(scope)
        }

        override fun startFirstStopwatch() {
            firstStopwatch.start()
        }

        override fun pauseFirstStopwatch() {
            firstStopwatch.pause()
        }

        override fun stopFirstStopwatch() {
            firstStopwatch.stop()
        }

        override fun startSecondStopwatch() {
            secondStopwatch.start()
        }

        override fun pauseSecondStopwatch() {
            secondStopwatch.pause()
        }

        override fun stopSecondStopwatch() {
            secondStopwatch.stop()
        }
    }
}