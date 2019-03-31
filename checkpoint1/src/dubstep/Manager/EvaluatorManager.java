package dubstep.Manager;

import dubstep.TreeNode.Tuple;
import net.sf.jsqlparser.eval.Eval;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.PrimitiveType;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Lomo
 */

public class EvaluatorManager extends Eval implements ExpressionVisitor {
    Tuple tp;
    List<SelectExpressionItem> selectExpressionItems = new LinkedList<>();
    Expression whereExpression;
    PrimitiveValue result;
    String columnName;//not used actually
    Column column;

    HashMap<Expression,String> newNames = new HashMap<>();
    Map<String,Column> rawColumns = new HashMap<>();//for new column
    Map<String,PrimitiveValue> columnValues = new HashMap<>();//for new column
    List<ColumnDefinition> coldefinitions = new ArrayList<>();//for new column

    public EvaluatorManager(Expression whereExpression) {
        this.whereExpression = whereExpression;
//        whereExpression.accept(this);
    }

    public EvaluatorManager(List<SelectExpressionItem> seis){

        this.selectExpressionItems=seis;
        for(SelectExpressionItem sei:seis){
            this.newNames.put(sei.getExpression(),sei.getAlias());

        }
    }

    public  Tuple evaluateProjection(Tuple tuple){

        this.tp=tuple;
        boolean notContain=true;

        for(SelectExpressionItem sei:selectExpressionItems){
            sei.getExpression().accept(this);

            if(sei.getExpression() instanceof Addition||sei.getExpression() instanceof Multiplication){

                String newName = newNames.get(sei.getExpression());
                Column col = new Column();
                col.setColumnName(newName);
                ColumnDefinition coldef = new ColumnDefinition();
                ColDataType colDataType = new ColDataType();
                colDataType.setDataType("int");
                coldef.setColumnName(newName);
                coldef.setColDataType(colDataType);

                rawColumns.put(newName,col);
                columnValues.put(newName,result);

                for(ColumnDefinition def: this.coldefinitions){
                    if(def.getColumnName()==newName){
                        notContain=false;
                    }
                }

                if(notContain){
                    coldefinitions.add(coldef);
                }

            }

        }

        tp.setAll(rawColumns,columnValues,coldefinitions);
        return this.tp;
    }

    public PrimitiveValue evaluateAttr(Tuple tuple){

        this.tp=tuple;
        whereExpression.accept(this);
        return this.result;

    }


    @Override
    public PrimitiveValue eval(GreaterThan a) throws SQLException {

        return super.eval(a);

    }

    @Override
    public PrimitiveType assertNumeric(PrimitiveType found) throws SQLException {

        return super.assertNumeric(found);

    }

    @Override
    public PrimitiveType escalateNumeric(PrimitiveType lhs, PrimitiveType rhs) throws SQLException {

        return super.escalateNumeric(lhs, rhs);

    }

    @Override
    public PrimitiveValue missing(String element) throws SQLException {

        return super.missing(element);

    }

    @Override
    public PrimitiveValue eval(Expression e) throws SQLException {

        return super.eval(e);

    }

    @Override
    public PrimitiveType getPrimitiveType(PrimitiveValue e) throws SQLException {
        return super.getPrimitiveType(e);
    }

    @Override
    public PrimitiveValue arith(BinaryExpression e, ArithOp op) throws SQLException {
        return super.arith(e, op);
    }

    @Override
    public PrimitiveValue cmp(BinaryExpression e, CmpOp op) throws SQLException {
        return super.cmp(e, op);
    }

    @Override
    public PrimitiveValue bool(BinaryExpression e, BoolOp op) throws SQLException {
        return super.bool(e, op);
    }

    @Override
    public PrimitiveValue eval(Addition a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(Division a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(Multiplication a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(Subtraction a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(AndExpression a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(OrExpression a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(EqualsTo a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(NotEqualsTo a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(GreaterThanEquals a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(MinorThan a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(MinorThanEquals a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(DateValue v) {
        return super.eval(v);
    }

    @Override
    public PrimitiveValue eval(DoubleValue v) {
        return super.eval(v);
    }

    @Override
    public PrimitiveValue eval(LongValue v) {
        return super.eval(v);
    }

    @Override
    public PrimitiveValue eval(StringValue v) {
        return super.eval(v);
    }

    @Override
    public PrimitiveValue eval(TimestampValue v) {
        return super.eval(v);
    }

    @Override
    public PrimitiveValue eval(TimeValue v) {
        return super.eval(v);
    }

    @Override
    public PrimitiveValue eval(CaseExpression c) throws SQLException {
        return super.eval(c);
    }

    @Override
    public PrimitiveValue eval(WhenClause whenClause) throws SQLException {
        return super.eval(whenClause);
    }

    @Override
    public PrimitiveValue eval(AllComparisonExpression all) throws SQLException {
        return super.eval(all);
    }

    @Override
    public PrimitiveValue eval(AnyComparisonExpression any) throws SQLException {
        return super.eval(any);
    }

    @Override
    public PrimitiveValue eval(ExistsExpression exists) throws SQLException {
        return super.eval(exists);
    }

    @Override
    public PrimitiveValue eval(InExpression in) throws SQLException {
        return super.eval(in);
    }

    @Override
    public PrimitiveValue eval(Between between) throws SQLException {
        return super.eval(between);
    }

    @Override
    public PrimitiveValue eval(LikeExpression like) throws SQLException {
        return super.eval(like);
    }

    @Override
    public PrimitiveValue eval(Matches matches) throws SQLException {
        return super.eval(matches);
    }

    @Override
    public PrimitiveValue eval(BitwiseXor a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(BitwiseOr a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(BitwiseAnd a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(Concat a) throws SQLException {
        return super.eval(a);
    }

    @Override
    public PrimitiveValue eval(Function function) throws SQLException {
        return super.eval(function);
    }

    @Override
    public PrimitiveValue eval(InverseExpression inverse) throws SQLException {
        return super.eval(inverse);
    }

    @Override
    public PrimitiveValue eval(IsNullExpression isNull) throws SQLException {
        return super.eval(isNull);
    }

    @Override
    public PrimitiveValue eval(JdbcParameter jdbcParameter) throws SQLException {
        return super.eval(jdbcParameter);
    }

    @Override
    public PrimitiveValue eval(NullValue nullValue) throws SQLException {
        return super.eval(nullValue);
    }

    //Return the PrimitiveValue of column

    @Override
    public PrimitiveValue eval(Column column) throws SQLException {
//        这边是PLAYERS.WEIGHT
        PrimitiveValue columnValue=tp.getColumnValue(column.getColumnName());
        return columnValue;
    }

    @Override
    public void visit(NullValue nullValue) {

    }

    @Override
    public void visit(Function function) {

    }

    @Override
    public void visit(InverseExpression inverseExpression) {

    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {

    }

    @Override
    public void visit(DoubleValue doubleValue) {

    }

    @Override
    public void visit(LongValue longValue) {

    }

    @Override
    public void visit(DateValue dateValue) {

    }

    @Override
    public void visit(TimeValue timeValue) {

    }

    @Override
    public void visit(TimestampValue timestampValue) {

    }

    @Override
    public void visit(BooleanValue booleanValue) {

    }

    @Override
    public void visit(StringValue stringValue) {

    }

    @Override
    public void visit(Addition addition) {

        PrimitiveValue rightValue;

        if((addition.getLeftExpression() instanceof Addition)== true){

            addition.getLeftExpression().accept(this);

        }else if(addition.getLeftExpression() instanceof Column){

            String columnName = ((Column) addition.getLeftExpression()).getColumnName();
            this.result = this.tp.getColumnValue(columnName);

        }else{
            this.result = (PrimitiveValue) addition.getLeftExpression();
        }

        if(addition.getRightExpression() instanceof Column){

            String columnName = ((Column) addition.getRightExpression()).getColumnName();
            rightValue = this.tp.getColumnValue(columnName);

        }else {
            rightValue=(PrimitiveValue) addition.getRightExpression();
        }

        try {

            this.result = this.eval(new Addition(this.result, rightValue));// Boolean Result

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void visit(Division division) {

    }

    @Override
    public void visit(Multiplication multiplication) {
        PrimitiveValue rightValue;

        if((multiplication.getLeftExpression() instanceof Multiplication)== true){

            multiplication.getLeftExpression().accept(this);

        }else if(multiplication.getLeftExpression() instanceof Column){

            String columnName = ((Column) multiplication.getLeftExpression()).getColumnName();
            this.result = this.tp.getColumnValue(columnName);

        }else{
            this.result = (PrimitiveValue) multiplication.getLeftExpression();
        }

        if(multiplication.getRightExpression() instanceof Column){

            String columnName = ((Column) multiplication.getRightExpression()).getColumnName();
            rightValue = this.tp.getColumnValue(columnName);

        }else {
            rightValue=(PrimitiveValue) multiplication.getRightExpression();
        }

        try {

            this.result = this.eval(new Multiplication(this.result, rightValue));// Boolean Result

        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    @Override
    public void visit(Subtraction subtraction) {

    }

    @Override
    public void visit(AndExpression andExpression) {

    }

    @Override
    public void visit(OrExpression orExpression) {

    }

    @Override
    public void visit(Between between) {

    }

    @Override
    public void visit(EqualsTo equalsTo) {

    }

    // E.g. WHERE WEIGHT>200, greaterThan = WEIGHT>200
    // PS: Both WEIGHT>200 OR 200<WEIGHT will cast to WEIGHT>200, so, its safe to use leftExpression to get columnName
    @Override
    public void visit(GreaterThan greaterThan) {

        try{
            columnName=greaterThan.getLeftExpression().toString();
            this.column=this.tp.getRawColumn(columnName);
            PrimitiveValue columValue= this.eval(this.column);

//            if(columValue instanceof DateValue){
//                columValue=(DateValue)columValue;
//            }

            this.result=this.eval(new GreaterThan(columValue,greaterThan.getRightExpression()));// Boolean Result

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {

    }

    @Override
    public void visit(InExpression inExpression) {

    }

    @Override
    public void visit(IsNullExpression isNullExpression) {

    }

    @Override
    public void visit(LikeExpression likeExpression) {

    }

    @Override
    public void visit(MinorThan minorThan) {

    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {

    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {

    }

    @Override
    public void visit(Column column) {

        String colName = column.getColumnName();
        PrimitiveValue colValue = this.tp.getColumnValue(colName);
        ColumnDefinition columnDefinition = this.tp.getColdefinition(colName);
        Column col=this.tp.getRawColumn(colName);
        this.rawColumns.put(colName,col);
        this.columnValues.put(colName,colValue);
        if(this.coldefinitions.contains(columnDefinition)==false){

            this.coldefinitions.add(columnDefinition);
        }

    }

    @Override
    public void visit(SubSelect subSelect) {

    }

    @Override
    public void visit(CaseExpression caseExpression) {

    }

    @Override
    public void visit(WhenClause whenClause) {

    }

    @Override
    public void visit(ExistsExpression existsExpression) {

    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {

    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {

    }

    @Override
    public void visit(Concat concat) {

    }

    @Override
    public void visit(Matches matches) {

    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {

    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {

    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {

    }
}
