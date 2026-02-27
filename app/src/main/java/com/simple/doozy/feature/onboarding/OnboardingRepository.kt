package com.simple.doozy.feature.onboarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OnboardingRepository(
    private val datastore: DataStore<Preferences>
) {
    private val ONBOARDING_COMPLETED = booleanPreferencesKey("example_counter")

    val hasCompletedOnboarding: Flow<Boolean> =
        datastore.data.map { preferences -> preferences[ONBOARDING_COMPLETED] ?: false }

    suspend fun setOnBoardingCompleted(value: Boolean) {
        datastore.edit { preferences -> preferences[ONBOARDING_COMPLETED] = value }
    }
}