package com.example.data.repository

import com.example.data.local.DiaryDao
import com.example.data.model.DiaryEntry
import kotlinx.coroutines.flow.Flow

class DiaryRepository(private val diaryDao: DiaryDao) {
    val allEntries: Flow<List<DiaryEntry>> = diaryDao.getAllEntries()

    fun getEntryById(id: Long): Flow<DiaryEntry?> = diaryDao.getEntryById(id)

    suspend fun getEntryByIdDirect(id: Long): DiaryEntry? = diaryDao.getEntryByIdDirect(id)

    fun getEntriesForDay(startOfDay: Long, endOfDay: Long): Flow<List<DiaryEntry>> =
        diaryDao.getEntriesForDay(startOfDay, endOfDay)

    fun searchEntries(query: String): Flow<List<DiaryEntry>> = diaryDao.searchEntries(query)

    suspend fun insertEntry(entry: DiaryEntry): Long = diaryDao.insertEntry(entry)

    suspend fun updateEntry(entry: DiaryEntry) = diaryDao.updateEntry(entry)

    suspend fun deleteEntry(entry: DiaryEntry) = diaryDao.deleteEntry(entry)

    suspend fun deleteEntryById(id: Long) = diaryDao.deleteEntryById(id)
}
