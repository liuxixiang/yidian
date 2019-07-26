package com.linken.newssdk.data.ad.db.sqlite;

import android.database.SQLException;
import android.text.TextUtils;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.annotation.apihint.Experimental;
import org.greenrobot.greendao.query.CloseableListIterator;
import org.greenrobot.greendao.query.CountQuery;
import org.greenrobot.greendao.query.CursorQuery;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.LazyList;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;
import org.greenrobot.greendao.rx.RxQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caichen on 2018/1/11.
 */

public class QueryBuilderWrapper {

    private static String TAG = QueryBuilderWrapper.class.getSimpleName();
    private static final int DEFAULT_ERROR_VALUE = 0;

    private QueryBuilder mQueryBuilder;

    public QueryBuilderWrapper(QueryBuilder queryBuilder, String tag) {
        if (queryBuilder != null) {
            mQueryBuilder = queryBuilder;
        }
        if (TextUtils.isEmpty(tag)) {
            tag = AbstractDaoWrapper.class.getSimpleName();
        }
        TAG = tag + "-" + TAG;
    }

    /** Set to true to debug the SQL. */
    public static boolean LOG_SQL = QueryBuilder.LOG_SQL;

    /** Set to see the given values. */
    public static boolean LOG_VALUES = QueryBuilder.LOG_VALUES;

    /** Use a SELECT DISTINCT to avoid duplicate entities returned, e.g. when doing joins. */
    public QueryBuilderWrapper distinct() {
        if (!checkNull()) {
            mQueryBuilder = mQueryBuilder.distinct();
        }
        return this;
    }

    /**
     * If using Android's embedded SQLite, this enables localized ordering of strings
     * (see {@link #orderAsc(Property...)} and {@link #orderDesc(Property...)}). This uses "COLLATE LOCALIZED", which
     * is unavailable in SQLCipher (in that case, the ordering is unchanged).
     *
     * @see #stringOrderCollation
     */
    public QueryBuilderWrapper preferLocalizedStringOrder() {
        // SQLCipher 3.5.0+ does not understand "COLLATE LOCALIZED"
        if (!checkNull()) {
            mQueryBuilder = mQueryBuilder.preferLocalizedStringOrder();
        }
        return this;
    }

    /**
     * Customizes the ordering of strings used by {@link #orderAsc(Property...)} and {@link #orderDesc(Property...)}.
     * Default is "COLLATE NOCASE".
     *
     * @see #preferLocalizedStringOrder
     */
    public QueryBuilderWrapper stringOrderCollation(String stringOrderCollation) {
        // SQLCipher 3.5.0+ does not understand "COLLATE LOCALIZED"
        if (!checkNull()) {
            mQueryBuilder = mQueryBuilder.stringOrderCollation(stringOrderCollation);
        }
        return this;
    }

    /**
     * Adds the given conditions to the where clause using an logical AND. To create new conditions, use the properties
     * given in the generated dao classes.
     */
    public QueryBuilderWrapper where(WhereCondition cond, WhereCondition... condMore) {
        if (!checkNull()) {
            mQueryBuilder = mQueryBuilder.where(cond, condMore);
        }
        return this;
    }

    /**
     * Adds the given conditions to the where clause using an logical OR. To create new conditions, use the properties
     * given in the generated dao classes.
     */
    public QueryBuilderWrapper whereOr(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        if (!checkNull()) {
            mQueryBuilder = mQueryBuilder.whereOr(cond1, cond2, condMore);
        }
        return this;
    }

    /**
     * Creates a WhereCondition by combining the given conditions using OR. The returned WhereCondition must be used
     * inside {@link #where(WhereCondition, WhereCondition...)} or
     * {@link #whereOr(WhereCondition, WhereCondition, WhereCondition...)}.
     */
    public WhereCondition or(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        if (checkNull()) {
            return new WhereCondition.AbstractCondition() {
                @Override
                public void appendTo(StringBuilder builder, String tableAlias) {

                }
            };
        }
        return mQueryBuilder.or(cond1, cond2, condMore);
    }

    /**
     * Creates a WhereCondition by combining the given conditions using AND. The returned WhereCondition must be used
     * inside {@link #where(WhereCondition, WhereCondition...)} or
     * {@link #whereOr(WhereCondition, WhereCondition, WhereCondition...)}.
     */
    public WhereCondition and(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        if (checkNull()) {
            return new WhereCondition.AbstractCondition() {
                @Override
                public void appendTo(StringBuilder builder, String tableAlias) {

                }
            };
        }
        return mQueryBuilder.and(cond1, cond2, condMore);
    }

    /**
     * Expands the query to another entity type by using a JOIN. The primary key property of the primary entity for
     * this QueryBuilder is used to match the given destinationProperty.
     */
    public <T> Join<?, T> join(Class<T> destinationEntityClass, Property destinationProperty) {
        if (checkNull()) {
            return null;
        }
        return mQueryBuilder.join(destinationEntityClass, destinationProperty);
    }

    /**
     * Expands the query to another entity type by using a JOIN. The given sourceProperty is used to match the primary
     * key property of the given destinationEntity.
     */
    public <T> Join<?, T> join(Property sourceProperty, Class<T> destinationEntityClass) {
        if (checkNull()) {
            return null;
        }
        return mQueryBuilder.join(sourceProperty, destinationEntityClass);
    }

    /**
     * Expands the query to another entity type by using a JOIN. The given sourceProperty is used to match the given
     * destinationProperty of the given destinationEntity.
     */
    public <T> Join<?, T> join(Property sourceProperty, Class<T> destinationEntityClass, Property destinationProperty) {
        if (checkNull()) {
            return null;
        }
        return mQueryBuilder.join(sourceProperty, destinationEntityClass, destinationProperty);
    }

    /**
     * Expands the query to another entity type by using a JOIN. The given sourceJoin's property is used to match the
     * given destinationProperty of the given destinationEntity. Note that destination entity of the given join is used
     * as the source for the new join to add. In this way, it is possible to compose complex "join of joins" across
     * several entities if required.
     */
    public <T> Join<?, T> join(Join<?, ?> sourceJoin, Property sourceProperty, Class<T> destinationEntityClass,
                               Property destinationProperty) {
        if (checkNull()) {
            return null;
        }
        return mQueryBuilder.join(sourceJoin, sourceProperty, destinationEntityClass, destinationProperty);
    }

    /** Adds the given properties to the ORDER BY section using ascending order. */
    public QueryBuilderWrapper orderAsc(Property... properties) {
        if (!checkNull()) {
            mQueryBuilder = mQueryBuilder.orderAsc(properties);
        }
        return this;
    }

    /** Adds the given properties to the ORDER BY section using descending order. */
    public QueryBuilderWrapper orderDesc(Property... properties) {
        if (!checkNull()) {
            mQueryBuilder = mQueryBuilder.orderDesc(properties);
        }
        return this;
    }

    /** Adds the given properties to the ORDER BY section using the given custom order. */
    public QueryBuilderWrapper orderCustom(Property property, String customOrderForProperty) {
        if (!checkNull()) {
            mQueryBuilder = mQueryBuilder.orderCustom(property, customOrderForProperty);
        }
        return this;
    }

    /**
     * Adds the given raw SQL string to the ORDER BY section. Do not use this for standard properties: orderAsc and
     * orderDesc are preferred.
     */
    public QueryBuilderWrapper orderRaw(String rawOrder) {
        if (!checkNull()) {
            mQueryBuilder = mQueryBuilder.orderRaw(rawOrder);
        }
        return this;
    }

    /** Limits the number of results returned by queries. */
    public QueryBuilderWrapper limit(int limit) {
        if (!checkNull()) {
            mQueryBuilder = mQueryBuilder.limit(limit);
        }
        return this;
    }

    /**
     * Sets the offset for query results in combination with {@link #limit(int)}. The first {@code limit} results are
     * skipped and the total number of results will be limited by {@code limit}. You cannot use offset without limit.
     */
    public QueryBuilderWrapper offset(int offset) {
        if (!checkNull()) {
            mQueryBuilder = mQueryBuilder.offset(offset);
        }
        return this;
    }

    /**
     * Builds a reusable query object (Query objects can be executed more efficiently than creating a QueryBuilder for
     * each execution.
     */
    public Query<?> build() {
        if (checkNull()) {
            return null;
        }
        return mQueryBuilder.build();
    }

    /**
     * Builds a reusable query object for low level android.database.Cursor access.
     * (Query objects can be executed more efficiently than creating a QueryBuilder for each execution.
     */
    public CursorQuery buildCursor() {
        if (checkNull()) {
            return null;
        }
        return mQueryBuilder.buildCursor();
    }

    /**
     * Builds a reusable query object for deletion (Query objects can be executed more efficiently than creating a
     * QueryBuilder for each execution.
     */
    public DeleteQuery<?> buildDelete() {
        if (checkNull()) {
            return null;
        }
        return mQueryBuilder.buildDelete();
    }

    /**
     * Builds a reusable query object for counting rows (Query objects can be executed more efficiently than creating a
     * QueryBuilder for each execution.
     */
    public CountQuery<?> buildCount() {
        if (checkNull()) {
            return null;
        }
        return mQueryBuilder.buildCount();
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#list() list()}; see {@link Query#list()} for
     * details. To execute a query more than once, you should build the query and keep the {@link Query} object for
     * efficiency reasons.
     */
    public List<?> list() {
        if (checkNull()) {
            return new ArrayList<>();
        }
        try {
            return mQueryBuilder.list();
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(e, TAG);
            return new ArrayList<>();
        }
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#__InternalRx()}.
     */
    @Experimental
    public RxQuery<?> rx() {
        if (checkNull()) {
            return null;
        }
        return mQueryBuilder.rx();
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#__internalRxPlain()}.
     */
    @Experimental
    public RxQuery<?> rxPlain() {
        if (checkNull()) {
            return null;
        }
        return mQueryBuilder.rxPlain();
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#listLazy() listLazy()}; see
     * {@link Query#listLazy()} for details. To execute a query more than once, you should build the query and keep the
     * {@link Query} object for efficiency reasons.
     */
    public LazyList<?> listLazy() {
        if (checkNull()) {
            return null;
        }
        try {
            return mQueryBuilder.listLazy();
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(e, TAG);
            return null;
        }
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#listLazyUncached() listLazyUncached()}; see
     * {@link Query#listLazyUncached()} for details. To execute a query more than once, you should build the query and
     * keep the {@link Query} object for efficiency reasons.
     */
    public LazyList<?> listLazyUncached() {
        if (checkNull()) {
            return null;
        }
        try {
            return mQueryBuilder.listLazyUncached();
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(e, TAG);
            return null;
        }
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#listIterator() listIterator()}; see
     * {@link Query#listIterator()} for details. To execute a query more than once, you should build the query and keep
     * the {@link Query} object for efficiency reasons.
     */
    public CloseableListIterator<?> listIterator() {
        if (checkNull()) {
            return null;
        }
        try {
            return mQueryBuilder.listIterator();
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(e, TAG);
            return null;
        }
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#unique() unique()}; see {@link Query#unique()}
     * for details. To execute a query more than once, you should build the query and keep the {@link Query} object for
     * efficiency reasons.
     */
    public Object unique() {
        if (checkNull()) {
            return null;
        }
        try {
            return mQueryBuilder.unique();
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(e, TAG);
            return null;
        }
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#uniqueOrThrow() uniqueOrThrow()}; see
     * {@link Query#uniqueOrThrow()} for details. To execute a query more than once, you should build the query and
     * keep
     * the {@link Query} object for efficiency reasons.
     */
    public Object uniqueOrThrow() {
        if (checkNull()) {
            return null;
        }
        try {
            return mQueryBuilder.uniqueOrThrow();
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(e, TAG);
            return null;
        }
    }

    /**
     * Shorthand for {@link QueryBuilder#buildCount() buildCount()}.{@link CountQuery#count() count()}; see
     * {@link CountQuery#count()} for details. To execute a query more than once, you should build the query and keep
     * the {@link CountQuery} object for efficiency reasons.
     */
    public long count() {
        if (checkNull()) {
            return DEFAULT_ERROR_VALUE;
        }
        return mQueryBuilder.count();
    }

    private boolean checkNull() {
        return mQueryBuilder == null;
    }
}
