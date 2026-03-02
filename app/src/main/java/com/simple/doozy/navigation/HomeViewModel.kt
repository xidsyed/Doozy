package com.simple.doozy.navigation

import androidx.lifecycle.ViewModel
import com.simple.doozy.navigation.route.Route.AuthenticatedNav.HomeNav
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _activeTab = MutableStateFlow<HomeNav.BottomNavTab>(HomeNav.TodosTab)
    val activeTab = _activeTab.asStateFlow()

    fun updateTab(tab: HomeNav.BottomNavTab) {
        _activeTab.value = tab
    }
}
