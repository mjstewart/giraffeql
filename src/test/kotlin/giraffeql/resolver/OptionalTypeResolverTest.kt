package giraffeql.resolver

import graphql.schema.GraphQLTypeUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.test.assertNotNull

class OptionalTypeResolverTest {

    private data class TestCase(val kClass: KClass<*>, val expect: String, val description: String)

    @Test
    fun `return null when no resolver matches`() {
        val env = mockEnvironment(resolver = OptionalTypeResolver())

        data class Test(val x: String)

        assertThat(env.resolver.resolve(Test::class, env)).isNull()
        assertThat(env.resolver.resolve(getProperty(Test::class, "x").returnType, env)).isNull()
        assertThat(env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env)).isNull()
    }

    private fun runResolverTest(kType: KType, testCase: TestCase, env: TypeResolverEnvironment) {
        env.resolver.resolve(kType, env).also { actual ->
            assertNotNull(actual, "KType resolver: ${testCase.description}: expected '${testCase.expect}', got null'")
            val actualPrinted = GraphQLTypeUtil.simplePrint(actual).trim()
            assertThat(actualPrinted)
                    .withFailMessage("KType resolver: ${testCase.description}: expected '${testCase.expect}', got '$actualPrinted'")
                    .isEqualTo(testCase.expect)
        }
    }

    private fun runResolverTest(kProperty: KProperty1<*, *>, testCase: TestCase, env: TypeResolverEnvironment) {
        env.resolver.resolve(testCase.kClass, kProperty, env).also { actual ->
            assertNotNull(actual, "KProperty resolver: ${testCase.description}: expected '${testCase.expect}', got null'")
            val actualPrinted = GraphQLTypeUtil.simplePrint(actual).trim()
            assertThat(actualPrinted)
                    .withFailMessage("KProperty resolver: ${testCase.description}: expected '${testCase.expect}', got '$actualPrinted'")
                    .isEqualTo(testCase.expect)
        }
    }

    @Test
    fun `Optional should be removed and replaced with nullable gql type`() {
        val env = mockEnvironment(resolver =
        ComposedTypeResolver(listOf(OptionalTypeResolver(), ScalarTypeResolver())))

        data class Test1(val x: Optional<String?>)
        data class Test2(val x: Optional<String>?)
        data class Test3(val x: Optional<String?>?)
        data class Test4(val x: Optional<String>)

        listOf(
                TestCase(Test1::class, "String", "Test1"),
                TestCase(Test2::class, "String", "Test2"),
                TestCase(Test3::class, "String", "Test3"),
                TestCase(Test4::class, "String", "Test4")
        ).forEach { testCase ->
            getProperty(testCase.kClass, extractFirstPropName(testCase.kClass)).also { property ->
                runResolverTest(property.returnType, testCase, env)
                runResolverTest(property, testCase, env)
            }
        }
    }

    @Test
    fun `Remove and replace nested Optionals with a nullable gql type`() {
        val env = mockEnvironment(resolver =
        ComposedTypeResolver(listOf(OptionalTypeResolver(), ListLikeTypeResolver(), ScalarTypeResolver())))

        data class Test1(val x: Optional<Optional<Optional<String>>>)
        data class Test2(val x: Optional<Optional<Optional<String?>?>?>?)
        data class Test3(val x: Optional<List<List<List<String>>>>)
        data class Test4(val x: Optional<List<List<List<String?>?>?>?>?)
        data class Test5(val x: Optional<List<List<List<String>?>>>)

        listOf(
                TestCase(Test1::class, "String", "Test1"),
                TestCase(Test2::class, "String", "Test2"),

                // Only the first root level list type is considered the optional
                TestCase(Test3::class, "[[[String!]!]!]", "Test3"),
                TestCase(Test4::class, "[[[String]]]", "Test4"),
                TestCase(Test5::class, "[[[String!]]!]", "Test5")
        ).forEach { testCase ->
            getProperty(testCase.kClass, extractFirstPropName(testCase.kClass)).also { property ->
                runResolverTest(property.returnType, testCase, env)
                runResolverTest(property, testCase, env)
            }
        }
    }
}