package at.fh.swengb.scherngell

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NoteRepository {

    fun getNotes(token: String, lastSync: Long, success: (notesResponse: NotesResponse) -> Unit, error: (errorMessage: String) -> Unit) {
        NoteApi.retrofitService.notes(token, lastSync).enqueue(object: Callback<NotesResponse> {

            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                error("Api call failed")
            }

            override fun onResponse(call: Call<NotesResponse>, response: Response<NotesResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                } else {
                    error("There was an error")
                }
            }
        })
    }

    fun uploadNote (token: String, noteToUpload: Note, success: (note: Note) -> Unit, error: (errorMessage: String) -> Unit) {
        NoteApi.retrofitService.addOrUpdateNote(token, noteToUpload).enqueue(object : Callback<Note> {
            override fun onFailure(call: Call<Note>, t: Throwable) {
                error("Api call failed " + t.localizedMessage)
            }

            override fun onResponse(call: Call<Note>, response: Response<Note>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                } else {
                    error("There was an error " + response.message())
                }
            }
        })
    }

    fun addNote(context: Context, newNote: Note) {
        val db = NoteDatabase.getDatabse(context)
        db.NoteDao.insert(newNote)
    }

    fun getNoteById(context: Context, id: String): Note {
        val db = NoteDatabase.getDatabse(context)
        return db.NoteDao.findNoteById(id)
    }

    fun getAllNotes(context: Context): List<Note> {
        val db = NoteDatabase.getDatabse(context)
        return db.NoteDao.getAllNotes()
    }

    fun deleteAllNotes(context: Context) {
        val db = NoteDatabase.getDatabse(context)
        return db.NoteDao.deleteAllNotes()
    }
}