package giraffeql.resolver

import giraffeql.extensions.KClassName
import graphql.Scalars
import graphql.schema.GraphQLTypeUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

class IDTypeResolverTest {

    @Test
    fun `all resolvers return null when property name matches id by convention but has unsupported type`() {
        val env = mockEnvironment(resolver = ComposedTypeResolver(listOf(IDTypeResolver())))

        data class Test1(val id: Float)
        data class Test2(val ID: Float)
        data class Test3(val theid: Float)
        data class Test4(val theId: Float)
        data class Test5(val theID: Float)

        data class Test1Nullable(val id: Float?)
        data class Test2Nullable(val ID: Float?)
        data class Test3Nullable(val theid: Float?)
        data class Test4Nullable(val theId: Float?)
        data class Test5Nullable(val theID: Float?)

        listOf(
                Test1::class,
                Test2::class,
                Test3::class,
                Test4::class,
                Test5::class,
                Test1Nullable::class,
                Test2Nullable::class,
                Test3Nullable::class,
                Test4Nullable::class,
                Test5Nullable::class
        ).forEach { kClass ->
            assertThat(env.resolver.resolve(kClass, env)).isNull()

            val propertyKey = extractFirstPropName(kClass)

            assertThat(env.resolver.resolve(getProperty(kClass, propertyKey).returnType, env)).isNull()

            env.resolver.resolve(kClass, getProperty(kClass, propertyKey), env).also { actual ->
                assertThat(actual)
                        .withFailMessage("expect: null, got %s for test case ${kClass.simpleName}", actual)
                        .isNull()
            }
        }
    }

    @Test
    fun `all resolvers return null when property name does not match id by convention but has supported type`() {
        val env = mockEnvironment(resolver = ComposedTypeResolver(listOf(IDTypeResolver())))

        data class Test1(val ids: Int)
        data class Test2(val IDS: Int)
        data class Test3(val iDs: Int)
        data class Test4(val theIds: Int)
        data class Test5(val theids: Int)
        data class Test6(val THEIDS: Int)

        data class Test1Nullable(val ids: Int?)
        data class Test2Nullable(val IDS: Int?)
        data class Test3Nullable(val iDs: Int?)
        data class Test4Nullable(val theIds: Int?)
        data class Test5Nullable(val theids: Int?)
        data class Test6Nullable(val THEIDS: Int?)

        listOf(
                Test1::class,
                Test2::class,
                Test3::class,
                Test4::class,
                Test5::class,
                Test6::class,
                Test1Nullable::class,
                Test2Nullable::class,
                Test3Nullable::class,
                Test4Nullable::class,
                Test5Nullable::class,
                Test6Nullable::class
        ).forEach { kClass ->
            assertThat(env.resolver.resolve(kClass, env)).isNull()

            val propertyKey = extractFirstPropName(kClass)

            assertThat(env.resolver.resolve(getProperty(kClass, propertyKey).returnType, env)).isNull()

            env.resolver.resolve(kClass, getProperty(kClass, propertyKey), env).also { actual ->
                assertThat(actual)
                        .withFailMessage("expect: null, got %s for test case ${kClass.simpleName}", actual)
                        .isNull()
            }
        }
    }

    @Test
    fun `resolves when property name by id convention matches with a supported type`() {
        val env = mockEnvironment(resolver = ComposedTypeResolver(listOf(IDTypeResolver())))

        // the supported types
        data class Test1(val id: Int)
        data class Test2(val id: String)
        data class Test3(val id: Long)
        data class Test4(val id: UUID)

        // variations of property names
        data class Test5(val id: Int)
        data class Test6(val iD: Int)
        data class Test7(val Id: Int)
        data class Test8(val ID: Int)
        data class Test9(val theid: Int)
        data class Test10(val theiD: Int)
        data class Test11(val theId: Int)
        data class Test12(val theID: Int)

        listOf(
                Test1::class,
                Test2::class,
                Test3::class,
                Test4::class,
                Test5::class,
                Test6::class,
                Test7::class,
                Test8::class,
                Test9::class,
                Test10::class,
                Test11::class,
                Test12::class
        ).forEach { kClass ->
            assertThat(env.resolver.resolve(kClass, env)).isNull()

            val propertyKey = extractFirstPropName(kClass)

            assertThat(env.resolver.resolve(getProperty(kClass, propertyKey).returnType, env)).isNull()

            env.resolver.resolve(kClass, getProperty(kClass, propertyKey), env).also { actual ->
                assertThat(GraphQLTypeUtil.unwrapOne(actual))
                        .withFailMessage("expect: null, got %s for test case ${kClass.simpleName}", actual)
                        .isEqualTo(Scalars.GraphQLID)
            }
        }
    }

    @Test
    fun `resolves when property name by id convention matches with a supported nullable type`() {
        val env = mockEnvironment(resolver = ComposedTypeResolver(listOf(IDTypeResolver())))

        // the supported types
        data class Test1(val id: Int?)
        data class Test2(val id: String?)
        data class Test3(val id: Long?)
        data class Test4(val id: UUID?)

        // variations of property names
        data class Test5(val id: Int?)
        data class Test6(val iD: Int?)
        data class Test7(val Id: Int?)
        data class Test8(val ID: Int?)
        data class Test9(val theid: Int?)
        data class Test10(val theiD: Int?)
        data class Test11(val theId: Int?)
        data class Test12(val theID: Int?)

        listOf(
                Test1::class,
                Test2::class,
                Test3::class,
                Test4::class,
                Test5::class,
                Test6::class,
                Test7::class,
                Test8::class,
                Test9::class,
                Test10::class,
                Test11::class,
                Test12::class
        ).forEach { kClass ->
            assertThat(env.resolver.resolve(kClass, env)).isNull()

            val propertyKey = extractFirstPropName(kClass)

            assertThat(env.resolver.resolve(getProperty(kClass, propertyKey).returnType, env)).isNull()

            env.resolver.resolve(kClass, getProperty(kClass, propertyKey), env).also { actual ->
                assertThat(GraphQLTypeUtil.unwrapOne(actual))
                        .withFailMessage("expect: null, got %s for test case ${kClass.simpleName}", actual)
                        .isEqualTo(Scalars.GraphQLID)
            }
        }
    }

    @Test
    fun `cache is not used`() {
        val env = mockEnvironment(resolver = ComposedTypeResolver(listOf(IDTypeResolver())))

        data class TestId(val id: Int)
        env.resolver.resolve(TestId::class, extractFirstProp(TestId::class), env)

        assertThat(KClassName("TestId") in env.cache).isFalse()
    }
}