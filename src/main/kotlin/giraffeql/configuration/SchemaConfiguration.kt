package giraffeql.configuration

import giraffeql.extensions.getNameOrThrow
import giraffeql.extensions.isClass
import giraffeql.extensions.toKClass
import giraffeql.resolver.IDTypeResolver
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

interface SchemaConfiguration {
    fun packagesToScan(): List<String> = emptyList()

    fun isInputType(kClass: KClass<*>): Boolean = kClass.getNameOrThrow().name.endsWith("Input") && kClass.isClass()
}