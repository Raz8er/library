package com.library.backend.api

import com.library.backend.config.AdminUser
import com.library.backend.dto.author.AuthorCreateDTO
import com.library.backend.mapper.AuthorMapper.toDTO
import com.library.backend.mapper.AuthorMapper.toEntity
import com.library.backend.service.author.AuthorService
import com.library.backend.testbase.ControllerTestBase
import com.library.backend.utils.JWTUtils
import com.library.backend.utils.TestUser
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.CoreMatchers.containsStringIgnoringCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.LocalDate

@WebMvcTest(controllers = [AuthorController::class])
class AuthorControllerTest : ControllerTestBase() {
    @MockkBean
    private lateinit var authorService: AuthorService

    @Autowired
    private lateinit var adminUser: AdminUser

    companion object {
        private const val AUTHOR_API_PATH = "/api/v1/authors"
    }

    @Test
    fun `should create author with admin jwt`() {
        val dto = AuthorCreateDTO(name = "Test Author", dateOfBirth = LocalDate.now().minusYears(5))
        val entity = dto.toEntity()

        every { authorService.createAuthor(dto) } returns entity

        mockMvc
            .post(AUTHOR_API_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(dto)
                with(JWTUtils.getAdminJWT())
            }.andExpect {
                status { isCreated() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(entity.toDTO()))
                }
            }
    }

    @Test
    fun `should create author with admin user`() {
        val dto = AuthorCreateDTO(name = "Test Author", dateOfBirth = LocalDate.now().minusYears(5))
        val entity = dto.toEntity()

        every { authorService.createAuthor(dto) } returns entity

        mockMvc
            .post(AUTHOR_API_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(dto)
                with(JWTUtils.getAdminUser(adminUser))
            }.andExpect {
                status { isCreated() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(entity.toDTO()))
                }
            }
    }

    @Test
    fun `should get 401 when creating author with no authorization header`() {
        val dto = AuthorCreateDTO(name = "Test Author", dateOfBirth = LocalDate.now().minusYears(5))
        val entity = dto.toEntity()

        every { authorService.createAuthor(dto) } returns entity

        mockMvc
            .post(AUTHOR_API_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(dto)
            }.andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    fun `should get 401 when creating author with anonymous user`() {
        val dto = AuthorCreateDTO(name = "Test Author", dateOfBirth = LocalDate.now().minusYears(5))
        val entity = dto.toEntity()

        every { authorService.createAuthor(dto) } returns entity

        mockMvc
            .post(AUTHOR_API_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(dto)
                with(JWTUtils.getAnonymousUser())
            }.andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    fun `should get 401 when creating author with no admin user`() {
        val dto = AuthorCreateDTO(name = "Test Author", dateOfBirth = LocalDate.now().minusYears(5))
        val entity = dto.toEntity()

        every { authorService.createAuthor(dto) } returns entity
        val testUser = TestUser(username = "my-user", password = "my-password", roles = listOf("USER-ROLE"))

        mockMvc
            .post(AUTHOR_API_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(dto)
                with(JWTUtils.getTestUser(testUser))
            }.andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `should get 403 when creating author with author jwt`() {
        val dto = AuthorCreateDTO(name = "Test Author", dateOfBirth = LocalDate.now().minusYears(5))
        val entity = dto.toEntity()

        every { authorService.createAuthor(dto) } returns entity

        mockMvc
            .post(AUTHOR_API_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(dto)
                with(JWTUtils.getAuthorJWT())
            }.andExpect {
                status { isForbidden() }
                jsonPath("$.message") { value("Access Denied") }
            }
    }

    @Test
    fun `should return 400 when author date of birth has invalid format`() {
        val invalidJson =
            """
            {
                "name": "Test Author",
                "dateOfBirth": "2000-05-15"
            }
            """.trimIndent()

        mockMvc
            .post(AUTHOR_API_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = invalidJson
                with(JWTUtils.getAdminJWT())
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.message").value("Malformed JSON request")
                jsonPath("$.subErrors[0].field").value("dateOfBirth")
                jsonPath("$.subErrors[0].message") {
                    value(containsStringIgnoringCase("expected format: dd-MM-yyyy"))
                }
            }
    }
}
