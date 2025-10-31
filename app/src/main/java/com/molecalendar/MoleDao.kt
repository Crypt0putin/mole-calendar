package com.molecalendar

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MoleDao {
    @Query("SELECT * FROM moles ORDER BY addedDate DESC")
    fun getAllMoles(): LiveData<List<Mole>>

    @Query("SELECT * FROM moles WHERE id = :moleId")
    fun getMoleById(moleId: Long): LiveData<Mole>

    @Query("SELECT * FROM moles WHERE id = :moleId")
    suspend fun getMoleByIdSync(moleId: Long): Mole?

    @Insert
    suspend fun insert(mole: Mole): Long

    @Update
    suspend fun update(mole: Mole)

    @Delete
    suspend fun delete(mole: Mole)

    @Query("DELETE FROM moles WHERE id = :moleId")
    suspend fun deleteById(moleId: Long)
}
