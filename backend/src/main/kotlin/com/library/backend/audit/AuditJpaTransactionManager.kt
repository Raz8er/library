package com.library.backend.audit

import jakarta.persistence.EntityManagerFactory
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.TransactionDefinition
import javax.sql.DataSource

@Component
class AuditJpaTransactionManager(
    emf: EntityManagerFactory,
    private val dataSource: DataSource,
) : JpaTransactionManager(emf) {
    override fun doBegin(
        transaction: Any,
        definition: TransactionDefinition,
    ) {
        super.doBegin(transaction, definition)
        setAuditUserOnCurrentConnection(dataSource)
    }

    private fun setAuditUserOnCurrentConnection(dataSource: DataSource) {
        val currentUser = SecurityContextHolder.getContext().authentication?.name ?: return
        val safe = currentUser.replace("'", "''")
        val conn = DataSourceUtils.getConnection(dataSource)
        conn.createStatement().use {
            it.execute("SET LOCAL my.audit_user = '$safe'")
        }
    }
}
