package demo.domain

import giraffeql.annotation.GQLType
import java.time.LocalDateTime
import java.util.*

@GQLType
data class Order(
        val id: UUID,
        val name: String,
        val createdOn: LocalDateTime
)