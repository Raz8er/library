package com.library.backend.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.TransactionDefinition
import javax.sql.DataSource

@Configuration
class AuditTransactionConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun transactionManager(
        emf: EntityManagerFactory,
        dataSource: DataSource,
    ): JpaTransactionManager =
        object : JpaTransactionManager(emf) {
            override fun doBegin(
                transaction: Any,
                definition: TransactionDefinition,
            ) {
                super.doBegin(transaction, definition)
                setAuditUserOnCurrentConnection(dataSource)
            }

            private fun setAuditUserOnCurrentConnection(dataSource: DataSource) {
                val currentUser = SecurityContextHolder.getContext().authentication.name
                val safe = currentUser.replace("'", "''")
                val conn = DataSourceUtils.getConnection(dataSource)
                conn.createStatement().use {
                    it.execute("SET LOCAL my.audit_user = '$safe'")
                }
            }
        }
}
