package dubstep.Manager;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 *
 * @author Lomo
 *
 */

public class RelationManager implements ExpressionVisitor{

    /**
     *
     * ---For EquiJoin ---
     *
     * Column leftCol;
     * Column rightCol;
     *
     * ---For Index Scan ---
     * String tableName,
     * String colName,
     * PrimitiveValue lowerBound,
     * PrimitiveValue upperBound,
     * boolean equals,
     * boolean softLowerBound,
     * boolean softUpperBound
     *
     */

    Column leftCol;
    Column rightCol;
    PrimitiveValue lowerBound = null;
    PrimitiveValue upperBound = null;
    boolean softLowerBound=false;
    boolean softUpperBound=false;
    boolean equals;
    String colName;
    String tableName;

    public RelationManager(Expression expression,String tableName) {
        this.tableName=tableName;
        expression.accept(this);
    }

    //  For EquiJoin
    public Column getLeftCol() {
        return leftCol;
    }

    public Column getRightCol() {
        return rightCol;
    }

    // For Index Scan
    public String getColName() {
        return colName;
    }

    public PrimitiveValue getLowerBound() {
        return lowerBound;
    }

    public PrimitiveValue getUpperBound() {
        return upperBound;
    }

    public boolean getSoftLowerBound() {
        return softLowerBound;
    }

    public boolean getSoftUpperBound() {
        return softUpperBound;
    }

    public boolean getEquals() {
        return equals;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void visit(Between between) {

    }

    // CASE 1: MAN.WEIGHT = WOMAN.WEIGHT
    // CASE 2: MAN.WEIGHT = 10


    @Override
    public void visit(EqualsTo equalsTo) {
        Expression lfh = equalsTo.getLeftExpression();
        Expression rhs = equalsTo.getRightExpression();

        String whereTbName = ((Column) lfh).getTable().getName();

        if(whereTbName.equals(this.tableName)){
            if(lfh instanceof Column){
//                colName = ((Column) lfh).getWholeColumnName();
                colName = ((Column) lfh).getColumnName();


            }

            if(rhs instanceof PrimitiveValue){
                try{
                    equals = ((PrimitiveValue) rhs).toBool();
                }catch (PrimitiveValue.InvalidPrimitive e){
                    e.printStackTrace();
                }
            }
        }



        if(lfh instanceof Column && rhs instanceof Column){

            this.leftCol=(Column)lfh;
            this.rightCol=(Column)rhs;

        }
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        Expression lfh = greaterThan.getLeftExpression();
        Expression rhs = greaterThan.getRightExpression();

        String whereTbName = ((Column) lfh).getTable().getName();

        if(whereTbName.equals(this.tableName)){
            if(lfh instanceof Column){
//                colName = ((Column) lfh).getWholeColumnName();
                colName = ((Column) lfh).getColumnName();


            }

            if(rhs instanceof PrimitiveValue){
                lowerBound = (PrimitiveValue) rhs;
            }
        }
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        Expression lfh = greaterThanEquals.getLeftExpression();
        Expression rhs = greaterThanEquals.getRightExpression();

        String whereTbName = ((Column) lfh).getTable().getName();

        if(whereTbName.equals(this.tableName)){
            if(lfh instanceof Column){
//                colName = ((Column) lfh).getWholeColumnName();
                colName = ((Column) lfh).getColumnName();


            }

            if(rhs instanceof PrimitiveValue){
                lowerBound = (PrimitiveValue) rhs;
                softLowerBound=true;
            }
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
        Expression lfh = minorThan.getLeftExpression();
        Expression rhs = minorThan.getRightExpression();

        String whereTbName = ((Column) lfh).getTable().getName();

        if(whereTbName.equals(this.tableName)){
            if(lfh instanceof Column){
//                colName = ((Column) lfh).getWholeColumnName();
                colName = ((Column) lfh).getColumnName();


            }

            if(rhs instanceof PrimitiveValue){
                upperBound = (PrimitiveValue) rhs;
            }
        }
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {

        Expression lfh = minorThanEquals.getLeftExpression();
        Expression rhs = minorThanEquals.getRightExpression();

        String whereTbName = ((Column) lfh).getTable().getName();

        if(whereTbName.equals(this.tableName)){

            if(lfh instanceof Column){

                colName = ((Column) lfh).getColumnName();

            }

            if(rhs instanceof PrimitiveValue){

                upperBound = (PrimitiveValue) rhs;
                softUpperBound=true;

            }
        }

    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {

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

    }

    @Override
    public void visit(Division division) {

    }

    @Override
    public void visit(Multiplication multiplication) {

    }

    @Override
    public void visit(Subtraction subtraction) {

    }

    @Override
    public void visit(AndExpression andExpression) {

        Expression lhs= andExpression.getLeftExpression();
        lhs.accept(this);
        Expression rhs= andExpression.getRightExpression();
        rhs.accept(this);

    }

    @Override
    public void visit(OrExpression orExpression) {

    }

    @Override
    public void visit(Column column) {

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
