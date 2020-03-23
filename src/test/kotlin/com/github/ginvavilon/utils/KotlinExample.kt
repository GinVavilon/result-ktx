
import com.github.ginvavilon.utils.Result
import com.github.ginvavilon.utils.tryCatch

class KotlinExample {

    companion object {
        @JvmStatic
        fun parseValue(string: String): Result<Int, NumberFormatException> {
            return tryCatch { string.toInt() }
        }

    }
}