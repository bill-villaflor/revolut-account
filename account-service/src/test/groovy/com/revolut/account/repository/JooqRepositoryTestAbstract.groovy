package com.revolut.account.repository

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.ClassicConfiguration
import org.h2.jdbcx.JdbcDataSource
import org.jooq.SQLDialect
import org.jooq.impl.DefaultDSLContext
import spock.lang.Shared
import spock.lang.Specification

class JooqRepositoryTestAbstract extends Specification {
    @Shared
    def dataSource = new JdbcDataSource(
            url: 'jdbc:h2:mem:account_test_db;DB_CLOSE_DELAY=-1',
            user: 'sa',
            password: ''
    )

    @Shared
    def flyway = new Flyway(new ClassicConfiguration(dataSource: dataSource))

    @Shared
    def create = new DefaultDSLContext(dataSource, SQLDialect.H2)

    def setupSpec() {
        flyway.migrate()
    }
}
