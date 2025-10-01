package com.library.authserver.api

import com.library.authserver.dto.client.ClientRequestDTO
import com.library.authserver.dto.client.ClientResponseDTO
import com.library.authserver.service.ClientService
import com.library.authserver.utils.ResponseEntityUtils
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/clients")
class ClientController(
    private val clientService: ClientService,
) {
    @PostMapping
    fun registerClient(
        @Valid @RequestBody clientRequest: ClientRequestDTO,
    ): ResponseEntity<ClientResponseDTO> =
        ResponseEntityUtils.createResponse(clientService.registerClient(clientRequest), HttpStatus.CREATED)
}
