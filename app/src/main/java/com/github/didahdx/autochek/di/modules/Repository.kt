package com.github.didahdx.autochek.di.modules

import com.github.didahdx.autochek.data.repository.CarRepository
import com.github.didahdx.autochek.data.repository.CarRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author Daniel Didah on 6/13/22
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class Repository {
    @Binds
    @Singleton
    abstract fun bindCarRepository(shopRepositoryImpl: CarRepositoryImpl): CarRepository
}