package com.jenzz.peoplenotes.common.data.di

import com.jenzz.peoplenotes.common.data.time.Clock
import com.jenzz.peoplenotes.common.data.time.SystemClock
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ClockModule {

    @Binds
    fun SystemClock.bind(): Clock
}