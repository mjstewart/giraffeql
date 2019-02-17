package giraffeql.resolver

import giraffeql.extensions.getNameOrThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ComposedTypeResolverTest {

    @Test
    fun `default resolvers`() {
        val resolvers = ComposedTypeResolver.defaultResolver().resolvers.map { it::class.getNameOrThrow().name  }.toSet()

        assertThat(resolvers.size).isEqualTo(6)
        assertThat("ScalarTypeResolver" in resolvers).isTrue()
        assertThat("ObjectTypeResolver" in resolvers).isTrue()
        assertThat("InputObjectTypeResolver" in resolvers).isTrue()
        assertThat("EnumTypeResolver" in resolvers).isTrue()
        assertThat("ListLikeTypeResolver" in resolvers).isTrue()
        assertThat("OptionalTypeResolver" in resolvers).isTrue()
    }
}