package giraffeql.exceptions

import kotlin.reflect.KClass

class MissingKClassNameException(source: KClass<*>) : GiraffeqlException("No class name for $source")