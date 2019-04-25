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
import net.sf.jsqlparser.schema.Table;
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

    HashMap<String,String> alias = new HashMap<>();
    Map<String,Column> rawColumns = new HashMap<>();//for new column
    Map<String,PrimitiveValue> columnValues = new HashMap<>();//for new column
    List<ColumnDefinition> coldefinitions = new ArrayList<>();//for new column
    List<Column> columnList = new ArrayList<>();

    public EvaluatorManager(){
        super();
    }
    public EvaluatorManager(Expression whereExpression) {
        this.whereExpression = whereExpression;
//        whereExpression.accept(this);
    }

    public EvaluatorManager(List<SelectExpressionItem> seis){

        this.selectExpressionItems=seis;
        for(SelectExpressionItem sei:seis){
            this.alias.put(sei.getExpression().toString(),sei.getAlias());

        }
    }


    public  Tuple evaluateProjection(Tuple tuple){

        this.tp=tuple;

        for(SelectExpressionItem sei:selectExpressionItems){

            sei.getExpression().accept(this);

        }

        tp.setAll(this.alias,this.columnValues,this.coldefinitions,this.columnList);
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
        PrimitiveValue columnValue=tp.getColumnValue(column.getWholeColumnName());
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
        try{
            PrimitiveValue result =this.eval(addition);
            this.result=result;
            Column col = new Column();
            col.setColumnName(addition.toString());
            Table tb = new Table();
            col.setTable(tb);
            this.columnList.add(col);
            this.columnValues.put(addition.toString(),result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void visit(Division division) {

    }

    @Override
    public void visit(Multiplication multiplication) {
        try{
            PrimitiveValue result =this.eval(multiplication);
            this.result=result;
            Column col = new Column();
            col.setColumnName(multiplication.toString());
            Table tb = new Table();
            col.setTable(tb);
            this.columnList.add(col);
            this.columnValues.put(multiplication.toString(),result);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void visit(Subtraction subtraction) {
        try{
            this.result= this.eval(subtraction);
            this.result=result;
            Column col = new Column();
            col.setColumnName(subtraction.toString());
            Table tb = new Table();
            col.setTable(tb);
            this.columnList.add(col);
            this.columnValues.put(subtraction.toString(),result);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    @Override
    public void visit(AndExpression andExpression) {
        try {

            this.result=this.eval(andExpression);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void visit(OrExpression orExpression) {

    }

    @Override
    public void visit(Between between) {

    }

    @Override
    public void visit(EqualsTo equalsTo) {
        try{
            this.result=this.eval(equalsTo);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // E.g. WHERE WEIGHT>200, greaterThan = WEIGHT>200
    // PS: Both WEIGHT>200 OR 200<WEIGHT will cast to WEIGHT>200, so, its safe to use leftExpression to get columnName
    @Override
    public void visit(GreaterThan greaterThan) {

        try{
            this.result= this.eval(greaterThan);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        try{
            this.result= this.eval(greaterThanEquals);
        }catch (SQLException e){
            e.printStackTrace();
        }
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
        try{

            this.result = this.eval(minorThan);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        try{

            this.result = this.eval(minorThanEquals);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        try{

            this.result = this.eval(notEqualsTo);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void visit(Column column) {
        try{

            PrimitiveValue colValue=this.eval(column);

            this.columnValues.put(column.getWholeColumnName().toString(),colValue);
            this.result=colValue;
        }catch (Exception e){

        }

        if(this.columnList.contains(column)==false){

            this.columnList.add(column);
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
