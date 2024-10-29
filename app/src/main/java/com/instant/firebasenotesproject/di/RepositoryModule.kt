package com.instant.firebasenotesproject.di
import com.instant.firebasenotesproject.repository.NotesRepository
import com.instant.firebasenotesproject.repository.NotesRepositoryImpl
import com.instant.firebasenotesproject.repository.UserRepository
import com.instant.firebasenotesproject.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)  // Bind it in the application's lifecycle
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository


    @Binds
    @Singleton
    abstract fun bindNotesRepository(
        notesRepositoryImpl: NotesRepositoryImpl
    ): NotesRepository
}
