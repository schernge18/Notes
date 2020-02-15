package at.fh.swengb.scherngell

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        val ACCESSTOKEN = "ACCESSTOKEN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)

        if (sharedPreferences.getString(ACCESSTOKEN, null) == null) {
            Log.e("AccessToken", "No Access Token found.")
        } else {
            val intent = Intent(this, NoteListActivity::class.java)
            startActivity(intent)
        }

        login_button.setOnClickListener {
            if (username_input.text.toString().isNotEmpty() && password_input.text.toString().isNotEmpty()) {
                val authRequest = AuthRequest(username_input.text.toString(), password_input.text.toString())

                login(authRequest,
                    success = {
                        sharedPreferences.edit().putString(ACCESSTOKEN, it.token).apply()
                        val intent = Intent(this, NoteListActivity::class.java)
                        startActivity(intent)
                    },
                    error = {
                        Log.e("Error", it)
                    })
            } else {
                Toast.makeText(this, "Please enter Username and Password to proceed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login(request: AuthRequest, success: (response: AuthResponse) -> Unit, error: (errorMessage: String) -> Unit) {
        NoteApi.retrofitService.login(request).enqueue(object: retrofit2.Callback<AuthResponse> {

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                error("Log in failed")
            }

            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                } else {
                    error("There was an error")
                }
            }
        })
    }
}
