package com.jedlab.framework.db.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.jedlab.framework.db.query.QueryController.GroupOperand;
import com.jedlab.framework.db.query.QueryController.LogicalOperand;
import com.jedlab.framework.db.query.WhereClause.QueryParam;
import com.jedlab.framework.util.CollectionUtil;

/**
 * @author Omid Pourhadi
 *
 */
public class WhereClause implements Iterable<QueryParam>
{

    List<QueryParam> params;

    /**
     * apply between all query params
     */
    private LogicalOperand logicalOperand = LogicalOperand.AND;

    /**
     * between operand
     */
    private LogicalOperand logicalOperandClause = LogicalOperand.AND;

    /**
     * apply between around each where clause
     */
    private GroupOperand groupOperand = GroupOperand.NOGROUP;

    public WhereClause(QueryParam... param)
    {
        this(Arrays.asList(param));
    }

    public WhereClause(List<QueryParam> parameters)
    {
        this.params = parameters;
    }

    public void add(QueryParam qp)
    {
        if (CollectionUtil.isEmpty(params))
            params = new ArrayList<WhereClause.QueryParam>();
        params.add(qp);
    }

    public LogicalOperand getLogicalOperand()
    {
        return logicalOperand;
    }

    public void setLogicalOperand(LogicalOperand logicalOperand)
    {
        this.logicalOperand = logicalOperand;
    }

    public GroupOperand getGroupOperand()
    {
        return groupOperand;
    }

    public void setGroupOperand(GroupOperand groupOperand)
    {
        this.groupOperand = groupOperand;
    }

    public LogicalOperand getLogicalOperandClause()
    {
        return logicalOperandClause;
    }

    public void setLogicalOperandClause(LogicalOperand logicalOperandClause)
    {
        this.logicalOperandClause = logicalOperandClause;
    }

    @Override
    public Iterator<QueryParam> iterator()
    {
        return this.params.iterator();
    }

    public static class QueryParam implements Serializable
    {
        private String columnExpression;
        private Operator operator;
        private Function function;

        /**
         * can be seam expression also
         */
        private Object value;

        public QueryParam(String columnExpression, Object value, Operator operator)
        {
            this.columnExpression = columnExpression;
            this.operator = operator;
            this.value = value;
        }

        public QueryParam(String columnExpression, Object value, Operator operator, Function function)
        {
            this.columnExpression = columnExpression;
            this.operator = operator;
            this.value = value;
            this.function = function;
        }

        public Function getFunction()
        {
            return function;
        }

        public String getColumnExpression()
        {
            return columnExpression;
        }

        public Operator getOperator()
        {
            return operator;
        }

        public Object getValue()
        {
            return value;
        }

    }

}
