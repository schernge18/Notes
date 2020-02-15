package at.fh.swengb.scherngell

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_edit_note.*

class AddEditNoteActivity : AppCompatActivity() {

    companion object {
        val EXTRA_ADD_OR_EDIT_RESULT = "ADD_OR_EDIT_RESULT"
        val ACCESSTOKEN = "ACCESSTOKEN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        val idExtra: String? = intent.getStringExtra(NoteListActivity.NOTEID)

        if (idExtra != null) {
            val note: Note? = NoteRepository.getNoteById(this, idExtra)
            if (note != null) {
                add_edit_title.setText(note.title)
                add_edit_text.setText(note.text)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item?.itemId) {
            R.id.save_note -> {

                val idExtra: String? = intent.getStringExtra(NoteListActivity.NOTEID)
                val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                val token = sharedPreferences.getString(ACCESSTOKEN, null)

                if (idExtra != null
                    && add_edit_text.text.isNotEmpty() && add_edit_title.text.isNotEmpty()
                    && token != null) {

                    val note = Note(idExtra, add_edit_title.text.toString(), add_edit_text.text.toString(), true)
                    NoteRepository.addNote(this, note)
                    NoteRepository.uploadNote(
                        token,
                        note,
                        success = {
                            NoteRepository.addNote(this, it)
                        },
                        error = {
                            Log.e("Upload error", it)
                        })


                    val resultIntent = intent
                    resultIntent.putExtra(EXTRA_ADD_OR_EDIT_RESULT, "DONE")
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()

                } else {
                    Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
