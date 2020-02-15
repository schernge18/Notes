package at.fh.swengb.scherngell

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_note_list.*
import java.util.*

class NoteListActivity : AppCompatActivity() {

    companion object {
        val ACCESSTOKEN = "ACCESSTOKEN"
        val LASTSYNC = "LASTSYNC"
        val NOTEID = "NOTEID"
        val EXTRA_ADD_OR_EDIT_REQUEST = 0
    }

    val noteAdapter = NoteAdapter {
        val intent = Intent(this, AddEditNoteActivity::class.java)
        intent.putExtra(NOTEID, it.id)
        startActivityForResult(intent, EXTRA_ADD_OR_EDIT_REQUEST)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString(ACCESSTOKEN, null)
        val lastSync = sharedPreferences.getLong(LASTSYNC, 0)
        if (token != null){
            NoteRepository.getNotes(
                token,
                lastSync,
                success = {
                    it.notes.map { NoteRepository.addNote(this, it) }
                    sharedPreferences.edit().putLong(LASTSYNC, it.lastSync).apply()
                    noteAdapter.updateList(NoteRepository.getAllNotes(this))
                },
                error = {
                    Log.e("An error occured", it)
                    noteAdapter.updateList(NoteRepository.getAllNotes(this))
                })

            note_recycler_view.layoutManager = StaggeredGridLayoutManager(2, 1)
            note_recycler_view.adapter = noteAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item?.itemId) {
            R.id.logout -> {
                val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                NoteRepository.deleteAllNotes(this)
                finish()
                true}
            R.id.add_note -> {
                val uuidString = UUID.randomUUID().toString()
                val intent = Intent(this, AddEditNoteActivity::class.java)
                intent.putExtra(NOTEID, uuidString)
                startActivityForResult(intent, EXTRA_ADD_OR_EDIT_REQUEST)
                true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EXTRA_ADD_OR_EDIT_REQUEST && resultCode == Activity.RESULT_OK) {
            noteAdapter.updateList(NoteRepository.getAllNotes(this))
            note_recycler_view.layoutManager = StaggeredGridLayoutManager(2,1)
            note_recycler_view.adapter = noteAdapter
        }
    }
}

