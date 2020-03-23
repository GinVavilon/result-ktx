import com.github.ginvavilon.utils.Result
import com.github.ginvavilon.utils.toResult
import com.github.ginvavilon.utils.tryMap
import com.github.ginvavilon.utils.try_


fun transfer(value: Int): Result<String, Exception> {
    return Result
            .tryCatch(IllegalAccessException::class) {
                value.toString()
            }.tryMap(IllegalStateException::class, IllegalArgumentException::class) {
                it
            }.tryMap {
                it
            }.tryMap(Exception::class) {
                it
            }.tryMap {
                it
            }

    return Result.tryCatch { value.toString() }
}

fun testReturn(): Result<String, Exception> {
    val res = try_(Exception::class) {
        11.toString()
    } catch {
        return it.toResult()
    }

    return (res + "1").toResult();
}


fun main() {

    try_(RuntimeException::class) {
        "12345"
    }.flatMap {
        KotlinExample.parseValue(it)
    }.map(Int::toString)
            .map(String::reversed)

    KotlinExample.parseValue("123").map { it.toString() }

    KotlinExample.parseValue("123").tryMap {

    }.tryMap {

    }.tryMap {

    }.catch {

    }


    KotlinExample.parseValue("12").catch {
        -1
    }

}