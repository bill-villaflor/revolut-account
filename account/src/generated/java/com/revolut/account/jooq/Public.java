/*
 * This file is generated by jOOQ.
 */
package com.revolut.account.jooq;


import com.revolut.account.jooq.tables.Accounts;
import com.revolut.account.jooq.tables.Books;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = -150423573;

    /**
     * The reference instance of <code>PUBLIC</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>PUBLIC.ACCOUNTS</code>.
     */
    public final Accounts ACCOUNTS = com.revolut.account.jooq.tables.Accounts.ACCOUNTS;

    /**
     * The table <code>PUBLIC.BOOKS</code>.
     */
    public final Books BOOKS = com.revolut.account.jooq.tables.Books.BOOKS;

    /**
     * No further instances allowed
     */
    private Public() {
        super("PUBLIC", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Accounts.ACCOUNTS,
            Books.BOOKS);
    }
}
