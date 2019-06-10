/*
 * This file is generated by jOOQ.
 */
package com.revolut.account.jooq.tables;


import com.revolut.account.jooq.Indexes;
import com.revolut.account.jooq.Keys;
import com.revolut.account.jooq.Public;
import com.revolut.account.jooq.tables.records.BooksRecord;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class Books extends TableImpl<BooksRecord> {

    private static final long serialVersionUID = 1777313656;

    /**
     * The reference instance of <code>PUBLIC.BOOKS</code>
     */
    public static final Books BOOKS = new Books();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BooksRecord> getRecordType() {
        return BooksRecord.class;
    }

    /**
     * The column <code>PUBLIC.BOOKS.ID</code>.
     */
    public final TableField<BooksRecord, UUID> ID = createField("ID", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.BOOKS.AMOUNT</code>.
     */
    public final TableField<BooksRecord, BigDecimal> AMOUNT = createField("AMOUNT", org.jooq.impl.SQLDataType.DECIMAL(19, 4), this, "");

    /**
     * The column <code>PUBLIC.BOOKS.SOURCE</code>.
     */
    public final TableField<BooksRecord, UUID> SOURCE = createField("SOURCE", org.jooq.impl.SQLDataType.UUID, this, "");

    /**
     * The column <code>PUBLIC.BOOKS.DESTINATION</code>.
     */
    public final TableField<BooksRecord, UUID> DESTINATION = createField("DESTINATION", org.jooq.impl.SQLDataType.UUID, this, "");

    /**
     * The column <code>PUBLIC.BOOKS.CREATION_DATE</code>.
     */
    public final TableField<BooksRecord, OffsetDateTime> CREATION_DATE = createField("CREATION_DATE", org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE.precision(10).nullable(false), this, "");

    /**
     * Create a <code>PUBLIC.BOOKS</code> table reference
     */
    public Books() {
        this(DSL.name("BOOKS"), null);
    }

    /**
     * Create an aliased <code>PUBLIC.BOOKS</code> table reference
     */
    public Books(String alias) {
        this(DSL.name(alias), BOOKS);
    }

    /**
     * Create an aliased <code>PUBLIC.BOOKS</code> table reference
     */
    public Books(Name alias) {
        this(alias, BOOKS);
    }

    private Books(Name alias, Table<BooksRecord> aliased) {
        this(alias, aliased, null);
    }

    private Books(Name alias, Table<BooksRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Books(Table<O> child, ForeignKey<O, BooksRecord> key) {
        super(child, key, BOOKS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.CONSTRAINT_INDEX_3, Indexes.CONSTRAINT_INDEX_3C, Indexes.PRIMARY_KEY_3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<BooksRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<BooksRecord>> getKeys() {
        return Arrays.<UniqueKey<BooksRecord>>asList(Keys.CONSTRAINT_3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<BooksRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BooksRecord, ?>>asList(Keys.CONSTRAINT_3C, Keys.CONSTRAINT_3C7);
    }

    public Accounts constraint_3c() {
        return new Accounts(this, Keys.CONSTRAINT_3C);
    }

    public Accounts constraint_3c7() {
        return new Accounts(this, Keys.CONSTRAINT_3C7);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Books as(String alias) {
        return new Books(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Books as(Name alias) {
        return new Books(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Books rename(String name) {
        return new Books(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Books rename(Name name) {
        return new Books(name, null);
    }
}