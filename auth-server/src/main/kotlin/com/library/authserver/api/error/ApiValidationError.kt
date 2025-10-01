package com.library.authserver.api.error

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiValidationError(
    var obj: String? = null,
    var field: String? = null,
    var rejectedValue: Any? = null,
    var message: String? = null,
) : ApiSubError()
