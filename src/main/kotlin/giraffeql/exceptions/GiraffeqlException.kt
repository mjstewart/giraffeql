package giraffeql.exceptions

open class GiraffeqlException(message: String = "", throwable: Throwable? = null) : RuntimeException(message, throwable)