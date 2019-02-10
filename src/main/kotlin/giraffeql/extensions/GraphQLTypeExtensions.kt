package giraffeql.extensions

import graphql.schema.*
import kotlin.reflect.KType


fun GraphQLType.wrapNonNull(type: KType): GraphQLType = when {
    GraphQLTypeUtil.isNonNull(this) -> this
    type.isMarkedNullable -> this
    else -> GraphQLNonNull(this)
}

fun GraphQLType.removeNonNull(): GraphQLType = when {
    GraphQLTypeUtil.isNonNull(this) -> GraphQLTypeUtil.unwrapOne(this)
    else -> this
}

fun GraphQLType.allInputTypes(): Boolean = GraphQLTypeUtil.unwrapType(this).all { it is GraphQLInputType }

fun GraphQLType.allOutputTypes(): Boolean = GraphQLTypeUtil.unwrapType(this).all { it is GraphQLOutputType }