package com.smartherd.notesapproom.ui


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.navigation.Navigation
import com.smartherd.notesapproom.db.Note
import com.smartherd.notesapproom.db.NoteDatabase
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.ui.NavigationUI
import com.smartherd.notesapproom.R


class AddNoteFragment : BaseFragment() {

    private var note: Note? = null
    private var viewContainer:ViewGroup? = null
    lateinit var imm:InputMethodManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        viewContainer = container
        //get the input method manager service
        imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(addToolbar as Toolbar?)
        val actionBar=(activity as AppCompatActivity).supportActionBar
        val noteTitle = edit_text_title.text.toString().trim()

        actionBar?.setDisplayHomeAsUpEnabled(true)
        val navController=Navigation.findNavController(activity as AppCompatActivity,R.id.fragment)
        NavigationUI.setupActionBarWithNavController(activity as AppCompatActivity,navController)

        if(noteTitle.isEmpty())
            actionBar?.title = "Create a new Note"
        else
            actionBar?.title = noteTitle

        arguments?.let {
            note = AddNoteFragmentArgs.fromBundle(it).note
            edit_text_note.setText(note?.note)
            edit_text_title.setText(note?.title)

        }

        saveNote.setOnClickListener { view ->


            val noteBody = edit_text_note.text.toString().trim()

            if(noteTitle.isEmpty()){
                edit_text_title.error = "Title Required"
                edit_text_title.requestFocus()
                return@setOnClickListener
            }

            if (noteBody.isEmpty()){
                edit_text_note.error = "Note Required"
                edit_text_note.requestFocus()
                return@setOnClickListener
            }


            launch {
                context?.let {
                    val mNote = Note(noteTitle, noteBody)

                    if(note == null){
                        NoteDatabase(it).getNoteDao().addNote(mNote)
                    }else{
                        mNote.id = note!!.id
                        NoteDatabase(it).getNoteDao().updateNote(mNote)
                    }
                    imm.hideSoftInputFromWindow(viewContainer!!.windowToken, 0)
                    val action = AddNoteFragmentDirections.actionSaveNote()
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
                R.id.delete -> if(note!= null) deleteNote() else context?.toast("Cannot Deleted")
        }
        return super.onOptionsItemSelected(item)
    }
    private fun deleteNote() {
        val dialogBuilder = AlertDialog.Builder(context).apply {
            setTitle("Are you sure?")
            setIcon(R.drawable.alert_warning)
            setCancelable(true)
            setMessage("You cannot undo this operation")
            setPositiveButton("Yes"){_,_ ->
                launch {
                    NoteDatabase(context).getNoteDao().deleteNote(note!!)
                    //Snackbar.make(root,"Note deleted.",Snackbar.LENGTH_LONG).show()
                    val action = AddNoteFragmentDirections.actionSaveNote()
                    Navigation.findNavController(view!!).navigate(action)
                }
            }
            setNegativeButton("No"){_,_ ->
            }
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).apply {
            setBackgroundColor(Color.WHITE)
            setTextColor(Color.BLUE)
        }

       alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).apply {
            setBackgroundColor(Color.WHITE)
            setTextColor(Color.BLUE)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
        val deleteItem = menu.findItem(R.id.delete)
        deleteItem.isVisible = note != null
    }


}
