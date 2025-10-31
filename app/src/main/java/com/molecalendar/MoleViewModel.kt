package com.molecalendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MoleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MoleRepository
    val allMoles: LiveData<List<Mole>>

    init {
        val moleDao = MoleDatabase.getDatabase(application).moleDao()
        repository = MoleRepository(moleDao)
        allMoles = repository.allMoles
    }

    fun getMoleById(moleId: Long): LiveData<Mole> {
        return repository.getMoleById(moleId)
    }

    fun insert(mole: Mole, callback: (Long) -> Unit) = viewModelScope.launch {
        val id = repository.insert(mole)
        callback(id)
    }

    fun update(mole: Mole) = viewModelScope.launch {
        repository.update(mole)
    }

    fun delete(mole: Mole) = viewModelScope.launch {
        repository.delete(mole)
    }

    fun deleteById(moleId: Long) = viewModelScope.launch {
        repository.deleteById(moleId)
    }
}
