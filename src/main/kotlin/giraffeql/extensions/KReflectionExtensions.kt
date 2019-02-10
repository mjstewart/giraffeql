package giraffeql.extensions

import giraffeql.exceptions.MissingKClassNameException
import giraffeql.resolver.ScalarTypeResolver
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

inline class KClassName(val name: String)

fun KClass<*>.getNameOrThrow(): KClassName = simpleName?.let { KClassName(it) } ?: throw MissingKClassNameException(this)

fun KClass<*>.isEnum(): Boolean = isSubclassOf(Enum::class)

fun KClass<*>.isInterface(): Boolean = java.isInterface

fun KClass<*>.isAbstract(): Boolean = isAbstract

fun KClass<*>.isScalar(): Boolean = ScalarTypeResolver.isSupportedScalar(this)

fun KClass<*>.isClass(): Boolean {
    return isData || (!isEnum() && !isInterface() && !isAbstract() && !isScalar())
}

fun KType.toKClass(): KClass<*> = jvmErasure

fun KType.isListLike() = jvmErasure.isSubclassOf(List::class) || jvmErasure.java.isArray