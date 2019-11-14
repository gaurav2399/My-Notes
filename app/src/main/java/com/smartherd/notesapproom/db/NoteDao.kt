package com.smartherd.notesapproom.db

import androidx.room.*

@Dao
interface NoteDao {

    //functions to access the database

    @Insert
    suspend fun addNote(note: Note)

    @Query("select * from note order by id desc")
    suspend fun getAllNotes() : List<Note>

    @Insert
    suspend fun addMultipleNotes(vararg note: Note)

    @Update
    suspend fun updateNote(note : Note)

    @Delete
    suspend fun deleteNote(note: Note)

}