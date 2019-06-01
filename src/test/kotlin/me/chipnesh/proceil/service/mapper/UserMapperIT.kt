package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.UserModel
import me.chipnesh.proceil.service.dto.UserValueObject
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import org.assertj.core.api.Assertions.assertThat
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Integration tests for [UserMapper].
 */
@SpringBootTest(classes = [ProceilApp::class])
class UserMapperIT {

    @Autowired
    private lateinit var userMapper: UserMapper

    private lateinit var user: UserModel
    private lateinit var userDto: UserValueObject

    @BeforeEach
    fun init() {
        user = UserModel()
        user.login = DEFAULT_LOGIN
        user.password = RandomStringUtils.random(60)
        user.activated = true
        user.email = "johndoe@localhost"
        user.firstName = "john"
        user.lastName = "doe"
        user.imageUrl = "image_url"
        user.langKey = "en"

        userDto = UserValueObject(user)
    }

    @Test
    fun usersToUserDTOsShouldMapOnlyNonNullUsers() {
        val users = mutableListOf<UserModel?>()
        users.add(user)
        users.add(null)

        val userDTOS = userMapper.usersToUserDTOs(users)

        assertThat(userDTOS).isNotEmpty
        assertThat(userDTOS).size().isEqualTo(1)
    }

    @Test
    fun userDTOsToUsersShouldMapOnlyNonNullUsers() {
        val usersDto = mutableListOf<UserValueObject?>()
        usersDto.add(userDto)
        usersDto.add(null)

        val users = userMapper.userDTOsToUsers(usersDto)

        assertThat(users).isNotEmpty
        assertThat(users).size().isEqualTo(1)
    }

    @Test
    fun userDTOsToUsersWithAuthoritiesStringShouldMapToUsersWithAuthoritiesDomain() {
        val authoritiesAsString = mutableSetOf<String>()
        authoritiesAsString.add("ADMIN")
        userDto.authorities = authoritiesAsString

        val usersDto = mutableListOf<UserValueObject>()
        usersDto.add(userDto)

        val users = userMapper.userDTOsToUsers(usersDto)

        assertThat(users).isNotEmpty
        assertThat(users).size().isEqualTo(1)
        assertThat(users[0].authorities).isNotNull
        assertThat(users[0].authorities).isNotEmpty
        assertThat(users[0].authorities.first().name).isEqualTo("ADMIN")
    }

    @Test
    fun userDTOsToUsersMapWithNullAuthoritiesStringShouldReturnUserWithEmptyAuthorities() {
        userDto.authorities = null

        val usersDto = mutableListOf<UserValueObject>()
        usersDto.add(userDto)

        val users = userMapper.userDTOsToUsers(usersDto)

        assertThat(users).isNotEmpty
        assertThat(users).size().isEqualTo(1)
        assertThat(users[0].authorities).isNotNull
        assertThat(users[0].authorities).isEmpty()
    }

    @Test
    fun userDTOToUserMapWithAuthoritiesStringShouldReturnUserWithAuthorities() {
        val authoritiesAsString = mutableSetOf<String>()
        authoritiesAsString.add("ADMIN")
        userDto.authorities = authoritiesAsString

        val user = userMapper.userDTOToUser(userDto)

        assertNotNull(user)
        assertThat(user.authorities).isNotNull
        assertThat(user.authorities).isNotEmpty
        assertThat(user.authorities.first().name).isEqualTo("ADMIN")
    }

    @Test
    fun userDTOToUserMapWithNullAuthoritiesStringShouldReturnUserWithEmptyAuthorities() {
        userDto.authorities = null

        val user = userMapper.userDTOToUser(userDto)

        assertNotNull(user)
        assertThat(user.authorities).isNotNull
        assertThat(user.authorities).isEmpty()
    }

    @Test
    fun userDTOToUserMapWithNullUserShouldReturnNull() {
        assertNull(userMapper.userDTOToUser(null))
    }

    @Test
    fun testUserFromId() {
        assertThat(userMapper.userFromId(DEFAULT_ID)?.id).isEqualTo(DEFAULT_ID)
        assertNull(userMapper.userFromId(null))
    }

    companion object {
        private const val DEFAULT_LOGIN = "johndoe"

        private const val DEFAULT_ID = 1L
    }
}
