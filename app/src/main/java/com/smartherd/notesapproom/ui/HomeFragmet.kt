package com.smartherd.notesapproom.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartherd.notesapproom.R

import com.smartherd.notesapproom.db.NoteDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home_fragmet.*
import kotlinx.coroutines.launch


class HomeFragmet : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_fragmet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(home_toolbar as Toolbar?)
        val actionBar=(activity as AppCompatActivity).supportActionBar
        actionBar?.hide()
        val navController=Navigation.findNavController(activity as AppCompatActivity,R.id.fragment)
        NavigationUI.setupActionBarWithNavController(activity as AppCompatActivity,navController)

        recycler_view_notes.setHasFixedSize(true)
        launch {
            recycler_view_notes.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        // Scrolling up
                        add.visibility=View.INVISIBLE
                    } else {
                        // Scrolling down
                        add.visibility=View.VISIBLE
                    }
                }
            })
        }
        recycler_view_notes.layoutManager = GridLayoutManager(context,2)

        launch{
            context?.let {
                val notes = NoteDatabase(it).getNoteDao().getAllNotes()
                val adapter = NotesAdapter(notes)
                recycler_view_notes.adapter = adapter
                if (adapter.itemCount!=0)
                    emptyImage.visibility = View.INVISIBLE
            }

        }

        add.setOnClickListener{
            val action=HomeFragmetDirections.actionAddNote()
            Navigation.findNavController(it).navigate(action)
        }
    }

}
