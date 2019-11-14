package com.smartherd.notesapproom.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Note::class],
    version = 1
)
abstract class NoteDatabase :RoomDatabase(){

    //functions to get dao

    abstract fun getNoteDao(): NoteDao

    companion object{

        //volatile -> to make available to other threads immediately
        @Volatile private var instance:NoteDatabase? =null
        private val LOCK = Any()

        //when database with parenthesis call
        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance ?: builDatabase(context).also {
                instance = it
            }
        }

        private fun builDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NoteDatabase::class.java,
            "noteDatabase"
        ).build()
    }

}