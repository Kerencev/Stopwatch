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

    abstract val data: Flow<String>
    protected abstract val scope: CoroutineScope

    abstract fun start()
    abstract fun pause()
    abstract fun stop()

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }

    class MainViewModel(
        private val stopwatch: Stopwatch
    ) : BaseViewModel() {

        override val data = MutableStateFlow("")
        override val scope = CoroutineScope(Dispatchers.IO)

        init {
            stopwatch.ticker
                .onEach {
                    data.value = it
                }
                .launchIn(scope)
        }

        override fun start() {
            stopwatch.start()
        }

        override fun pause() {
            stopwatch.pause()
        }

        override fun stop() {
            stopwatch.stop()
        }
    }
}