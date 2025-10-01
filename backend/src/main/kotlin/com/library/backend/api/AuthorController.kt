package com.library.backend.api

import com.library.backend.dto.author.AuthorCreateDTO
import com.library.backend.dto.author.AuthorDTO
import com.library.backend.dto.author.AuthorUpdateDTO
import com.library.backend.mapper.AuthorMapper.toDTO
import com.library.backend.service.author.AuthorService
import com.library.backend.utils.ResponseEntityUtils
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/authors")
class AuthorController(
    private val authorService: AuthorService,
) {
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_admin') or hasRole('ADMIN')")
    fun createAuthor(
        @Valid @RequestBody dto: AuthorCreateDTO,
    ): ResponseEntity<AuthorDTO> = ResponseEntityUtils.createResponse(authorService.createAuthor(dto).toDTO(), HttpStatus.CREATED)

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_admin') or hasRole('ADMIN')")
    fun updateAuthor(
        @PathVariable id: Long,
        @Valid @RequestBody dto: AuthorUpdateDTO,
    ): ResponseEntity<AuthorDTO> = ResponseEntityUtils.createResponse(authorService.updateAuthor(id, dto).toDTO(), HttpStatus.OK)
}
