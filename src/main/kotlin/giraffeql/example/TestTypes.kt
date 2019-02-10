package giraffeql.example

import giraffeql.annotation.GQLType

//@GQLType
//interface Payable {
//    val weekly: Double
//}
//
//@GQLType
//abstract class User(var id: Long, val name: String)
//
//enum class GQLTypes {
//    QUERY, MUTATION, INTERFACE, UNION, ENUM
//}

@GQLType
class BasicUser(
//        val basicUserId: Long,
//        val age: Int,
//        val city: String?,
//        val names: Array<String>,
        val day: Weekdays
)

@GQLType
class BasicUserInput(val d: Weekdays)



@GQLType
enum class Weekdays {
    Mon,
    Tue,
    Wed,
    Thur,
    Fri
}

//@GQLType
//data class UserInput(val age: String, val city: String)

// What happens if signature exists elsewhere?. store hash or string and compare
//@GQLQuery(root = BasicUser::class)
//@GQLQuery
//interface UserService {
//    fun users(): List<BasicUser>
//    fun userById(id: Long): BasicUser
//    fun userByIdAndName(id: Long?, name: String = "joe"): BasicUser
//    fun usersByAgeAndCity(query: UserInput): List<BasicUser>
//
//    data class UserAgeQueryInput(val age: Int)
