package giraffeql.extensions

import giraffeql.exceptions.GiraffeqlException

class TypeResolverException(path: String) : GiraffeqlException("Can't resolve type at path: '$path'")