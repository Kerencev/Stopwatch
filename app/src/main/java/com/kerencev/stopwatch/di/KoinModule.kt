package com.kerencev.stopwatch.di

import com.kerencev.stopwatch.model.*
import com.kerencev.stopwatch.ui.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<TimestampProvider> { TimestampProvider.Base() }
    single<ElapsedTimeCalculator> { ElapsedTimeCalculator.Base(timestampProvider = get()) }
    single<StopwatchStateCalculator> {
        StopwatchStateCalculator.Base(
            timestampProvider = get(),
            elapsedTimeCalculator = get()
        )
    }
    single<TimestampMillisecondsFormatter> { TimestampMillisecondsFormatter.Base() }
    single<StopwatchStateHolder> {
        StopwatchStateHolder.Base(
            stopwatchStateCalculator = get(),
            elapsedTimeCalculator = get(),
            timestampMillisecondsFormatter = get()
        )
    }
    single<Stopwatch> { Stopwatch.StopwatchListOrchestrator(stopwatchStateHolder = get()) }

    viewModel<BaseViewModel> { BaseViewModel.MainViewModel(stopwatch = get()) }
}