package com.jenzz.peoplenotes.common.data.di

import com.jenzz.peoplenotes.common.data.CoroutineDispatchers
import com.jenzz.peoplenotes.common.data.DefaultCoroutineDispatchers
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CoroutineModule {

    @Binds
    fun DefaultCoroutineDispatchers.bind(): CoroutineDispatchers
}
