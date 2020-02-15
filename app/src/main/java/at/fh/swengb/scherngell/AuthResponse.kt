package at.fh.swengb.scherngell

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AuthResponse (val token: String) {
}