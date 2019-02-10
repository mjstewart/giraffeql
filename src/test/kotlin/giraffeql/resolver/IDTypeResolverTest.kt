package giraffeql.resolver

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class IDTypeResolverTest {

    @Test
    fun `return null when KType field name does not end in id convention`() {
        val env = mockEnvironment(resolver = ComposedTypeResolver(listOf(IDTypeResolver())))

        data class Test(val x: Int)

        Assertions.assertThat(env.resolver.resolve(getPropertyType(Test::class, "x"), env)).isNull()
    }

    @Test
    fun `return null when KType is not an ID`() {
        val env = mockEnvironment(resolver = ComposedTypeResolver(listOf(IDTypeResolver())))

        data class Test1(val id: String)
        data class Test2(val testId: String)

        Assertions.assertThat(env.resolver.resolve(getPropertyType(Test::class, "x"), env)).isNull()
    }


    @Test
    fun `resolve ID field`() {
        val env = mockEnvironment(resolver = ComposedTypeResolver(listOf(IDTypeResolver())))

        data class Test(val id: Long)

    }
}