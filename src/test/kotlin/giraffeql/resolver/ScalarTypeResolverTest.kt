package giraffeql.resolver

import graphql.schema.GraphQLTypeUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KType
import kotlin.test.assertNotNull


class ScalarTypeResolverTest {

    private fun runResolverTest(type: KType, expect: String) {
        val env = mockEnvironment(resolver = ScalarTypeResolver())

        env.resolver.resolve(type, env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo(expect)
        }
    }

    @Test
    fun `return null if not a scalar type for KType`() {
        val env = mockEnvironment(resolver = ScalarTypeResolver())

        data class NonScalarType(val y: String)
        data class Test(val x: NonScalarType)
        assertThat(env.resolver.resolve(getPropertyType(Test::class, "x"), env)).isNull()
    }

    @Test
    fun `return null if not a scalar type for KClass`() {
        val env = mockEnvironment(resolver = ScalarTypeResolver())

        data class Test(val x: Int)
        assertThat(env.resolver.resolve(Test::class, env)).isNull()
    }


    @Test
    fun `String to GraphQLString`() {
        data class TestNullable(val x: String?)
        runResolverTest(getPropertyType(TestNullable::class, "x"), "String")

        data class Test(val x: String)
        runResolverTest(getPropertyType(Test::class, "x"), "String!")
    }


    @Test
    fun `Boolean to GraphQLBoolean`() {
        data class TestNullable(val x: Boolean?)
        runResolverTest(getPropertyType(TestNullable::class, "x"), "Boolean")

        data class Test(val x: Boolean)
        runResolverTest(getPropertyType(Test::class, "x"), "Boolean!")
    }

    @Test
    fun `Int to GraphQLInt`() {
        data class TestNullable(val x: Int?)
        runResolverTest(getPropertyType(TestNullable::class, "x"), "Int")

        data class Test(val x: Int)
        runResolverTest(getPropertyType(Test::class, "x"), "Int!")
    }

    @Test
    fun `Float to GraphQLFloat`() {
        data class TestNullable(val x: Float?)
        runResolverTest(getPropertyType(TestNullable::class, "x"), "Float")

        data class Test(val x: Float)
        runResolverTest(getPropertyType(Test::class, "x"), "Float!")
    }

    @Test
    fun `Double to GraphQLFloat`() {
        data class TestNullable(val x: Double?)
        runResolverTest(getPropertyType(TestNullable::class, "x"), "Float")

        data class Test(val x: Double)
        runResolverTest(getPropertyType(Test::class, "x"), "Float!")
    }

    @Test
    fun `Long to GraphQLLong`() {
        data class TestNullable(val x: Long?)
        runResolverTest(getPropertyType(TestNullable::class, "x"), "Long")

        data class Test(val x: Long)
        runResolverTest(getPropertyType(Test::class, "x"), "Long!")
    }

    @Test
    fun `BigDecimal to GraphQLBigDecimal`() {
        data class TestNullable(val x: BigDecimal?)
        runResolverTest(getPropertyType(TestNullable::class, "x"), "BigDecimal")

        data class Test(val x: BigDecimal)
        runResolverTest(getPropertyType(Test::class, "x"), "BigDecimal!")
    }

    @Test
    fun `BigInteger to GraphQLBigInteger`() {
        data class TestNullable(val x: BigInteger?)
        runResolverTest(getPropertyType(TestNullable::class, "x"), "BigInteger")

        data class Test(val x: BigInteger)
        runResolverTest(getPropertyType(Test::class, "x"), "BigInteger!")
    }

    @Test
    fun `Byte to GraphQLByte`() {
        data class TestNullable(val x: Byte?)
        runResolverTest(getPropertyType(TestNullable::class, "x"), "Byte")

        data class Test(val x: Byte)
        runResolverTest(getPropertyType(Test::class, "x"), "Byte!")
    }

    @Test
    fun `Char to GraphQLChar`() {
        data class TestNullable(val x: Char?)
        runResolverTest(getPropertyType(TestNullable::class, "x"), "Char")

        data class Test(val x: Char)
        runResolverTest(getPropertyType(Test::class, "x"), "Char!")
    }

    @Test
    fun `Short to GraphQLChar`() {
        data class TestNullable(val x: Short?)
        runResolverTest(getPropertyType(TestNullable::class, "x"), "Short")

        data class Test(val x: Short)
        runResolverTest(getPropertyType(Test::class, "x"), "Short!")
    }
}