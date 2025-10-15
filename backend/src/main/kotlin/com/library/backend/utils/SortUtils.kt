package com.library.backend.utils

import com.library.backend.graphql.model.pagination.SortInput
import org.springframework.data.domain.Sort

object SortUtils {
    fun toSpringSort(
        sortInputs: List<SortInput>?,
        default: Sort,
    ): Sort {
        if (sortInputs == null || sortInputs.isEmpty()) return default

        val orders =
            sortInputs.map {
                val direction =
                    when (it.direction ?: SortInput.SortDirection.DESC) {
                        SortInput.SortDirection.ASC -> Sort.Direction.ASC
                        SortInput.SortDirection.DESC -> Sort.Direction.DESC
                    }
                Sort.Order(direction, it.field)
            }
        return Sort.by(orders)
    }
}
