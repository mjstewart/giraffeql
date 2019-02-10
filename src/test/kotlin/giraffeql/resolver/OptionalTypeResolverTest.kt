package giraffeql.resolver

import graphql.schema.GraphQLTypeUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertNotNull

class OptionalTypeResolverTest {

    @Test
    fun `return null if not an Optional type`() {
        val env = mockEnvironment(resolver = OptionalTypeResolver())

        data class Test(val x: String)

        assertThat(env.resolver.resolve(getPropertyType(Test::class, "x"), env)).isNull()
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
                Triple(Test1::class ,"String", "Optional<String?>"),
                Triple(Test2::class ,"String", "Optional<String>?"),
                Triple(Test3::class ,"String", "Optional<String?>?"),
                Triple(Test4::class ,"String", "Optional<String>")
        ).forEach {(kClass, expect, description) ->
            env.resolver.resolve(getPropertyType(kClass, "x"), env).also { actual ->
                assertNotNull(actual)
                val actualPrinted = GraphQLTypeUtil.simplePrint(actual).trim()
                assertThat(actualPrinted)
                        .withFailMessage("$description: expected '$expect', got '$actualPrinted'")
                        .isEqualTo(expect)
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
                Triple(Test1::class ,"String", "Optional<Optional<Optional<String>>>"),
                Triple(Test2::class ,"String", "Optional<Optional<Optional<String?>?>?>?"),

                // Only the first root level list type is considered the optional
                Triple(Test3::class ,"[[[String!]!]!]", "Optional<List<List<List<String>>>>"),
                Triple(Test4::class ,"[[[String]]]", "Optional<List<List<List<String?>?>?>?>?"),
                Triple(Test5::class ,"[[[String!]]!]", "Optional<List<List<List<String>?>>>")
        ).forEach {(kClass, expect, description) ->
            env.resolver.resolve(getPropertyType(kClass, "x"), env).also { actual ->
                assertNotNull(actual)
                val actualPrinted = GraphQLTypeUtil.simplePrint(actual).trim()
                assertThat(actualPrinted)
                        .withFailMessage("$description, expect $expect, got $actualPrinted")
                        .isEqualTo(expect)
            }
        }
    }
}