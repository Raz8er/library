package com.library.backend.audit

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.transaction.TransactionManager

@Configuration
class AuditTransactionConfig(
    private val auditJpaTransactionManager: AuditJpaTransactionManager,
) {
    @Bean
    @Primary
    fun transactionManager(): TransactionManager = auditJpaTransactionManager
}
