package com.molecalendar

import androidx.lifecycle.LiveData

class MoleRepository(private val moleDao: MoleDao) {
    val allMoles: LiveData<List<Mole>> = moleDao.getAllMoles()

    fun getMoleById(moleId: Long): LiveData<Mole> {
        return moleDao.getMoleById(moleId)
    }

    suspend fun getMoleByIdSync(moleId: Long): Mole? {
        return moleDao.getMoleByIdSync(moleId)
    }

    suspend fun insert(mole: Mole): Long {
        return moleDao.insert(mole)
    }

    suspend fun update(mole: Mole) {
        moleDao.update(mole)
    }

    suspend fun delete(mole: Mole) {
        moleDao.delete(mole)
    }

    suspend fun deleteById(moleId: Long) {
        moleDao.deleteById(moleId)
    }
}
