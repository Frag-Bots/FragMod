package xyz.fragbots.fragmod.util

fun hash(string: String): String {
    try {
        val md = java.security.MessageDigest.getInstance("sha1")
        val salt = java.math.BigInteger(130, java.util.Random()).toString(32)
        val salted = string + salt
        return java.math.BigInteger(md.digest(salted.toByteArray())).toString(32)
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}