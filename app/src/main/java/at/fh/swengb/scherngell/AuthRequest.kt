package at.fh.swengb.scherngell

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AuthRequest (val username: String,
                   val password: String) {
}