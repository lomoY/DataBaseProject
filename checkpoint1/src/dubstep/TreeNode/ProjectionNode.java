package dubstep.TreeNode;

import dubstep.Aggregator.AggrNode;
import dubstep.Manager.EvaluatorManager;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Lomo Yang
 */

public class ProjectionNode extends TreeNode implements SelectItemVisitor {
    boolean isGroupBy = false;
    String FromItemName;
    Table TableObj;
    List<SelectExpressionItem> projectAttrs = new ArrayList<>();
    List<String> renameAttrs = new ArrayList<String>();
    List<Column> groupByColumns;
    Schema schema;
    EvaluatorManager projectEm;
//    List<Column> LhsColumnList = new ArrayList<>();

    public ProjectionNode(PlainSelect plainSelect) {

//        set columns that need to be projected

        setSelectAttr(plainSelect.getSelectItems());

        // set columnList
        for(SelectExpressionItem sei:this.projectAttrs){

            Expression es = sei.getExpression();
            String alias = sei.getAlias();

            if(es instanceof Column) {

                if(alias!=null){
                    ((Column) es).setColumnName(alias);
                }

                this.LhsColumnList.add((Column) es);
            }

            if(es instanceof Function) {

                Column cl = new Column();
                Table tb = new Table();

                if(alias!=null){

                    cl.setColumnName(alias);

                }else{

                    cl.setColumnName(((Function) es).getName());
                }
                cl.setTable(tb);

                this.LhsColumnList.add(cl);

            }

        }

//        Set SelectionNode as Child
        Expression whereCondition = plainSelect.getWhere();
        if(whereCondition!=null){
            SelectionNode selectionNode=new SelectionNode(whereCondition);
            this.setLeftChildNode(selectionNode);
        }

//        Set TableNode as Child
        FromItem fromItem=plainSelect.getFromItem();
        FromItemNode fromItemNode = new FromItemNode(fromItem);


        List<Join> joins= plainSelect.getJoins();
        if(joins!= null) {
            JoinNode joinNode = new JoinNode(fromItemNode, joins);
            if (leftChildNode == null) {
                this.setLeftChildNode(joinNode);
            } else {
                this.leftChildNode.setLeftChildNode(joinNode);
            }
        } else {
//            Set TableNode as Child

            if(leftChildNode==null){
                this.setLeftChildNode(fromItemNode);
            }else{
                this.leftChildNode.setLeftChildNode(fromItemNode);
            }
        }


        this.groupByColumns=plainSelect.getGroupByColumnReferences();

    }

    /**
     * Iterator
     *
     * @return
     */

    private class Itr implements Iterator<Tuple> {
        Iterator<Tuple> lfir;
        List<SelectExpressionItem> projectAttrs;


        public Itr() {

            lfir = ProjectionNode.this.leftChildNode.iterator();
            projectAttrs = ProjectionNode.this.projectAttrs;

        }

        /**
         *
         * @return tuple accroding to the condition in schema(columndifinitions)
         *
         */

        @Override
        public Tuple next() {

            Tuple tp = lfir.next();

            if(projectAttrs.size()==0){//if not Players.*
            }else{
                projectEm = new EvaluatorManager(projectAttrs);
                tp=projectEm.evaluateProjection(tp);
            }

            return tp;
        }

        @Override
        public boolean hasNext() {

            if(lfir.hasNext()){
                return true;
            }else {
                return false;
            }

        }

        @Override
        public void remove() {}
        @Override
        public void forEachRemaining(Consumer action) {}

    }

    /**
     * SelectItem Visitor
     *
     * AllColumns: *
     * AllTableColumns: PLAYERS.*
     * SelectExpressionItem: A, PLAYERS.A
     *
     * @param allColumns
     */

    @Override
    public void visit(AllColumns allColumns) {
        // nothing need to be changed if select *
    }
    //  PLAYERS.*
    //  TABLE"PLAYERS"

    @Override
    public void visit(AllTableColumns allTableColumns) {
        // nothing need to be changed if select PLAYERS.*
    }

//    FIRSTNAME AS A
//    COLUMNNAME = "FIRSTNAME", ALIAS ="A"

//    PLAYERS.FIRSTNAME
//    COLUMNNAME = "FIRSTNAME",TABLE="PLAYERS"
//    COUNT(FIRSTNAME), if(name="COUNT"),parameters
//    SELECT LASTSEASON-FIRSTSEASON AS YEARS FROM PLAYERS;
    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {


        String alias=selectExpressionItem.getAlias();
        this.projectAttrs.add(selectExpressionItem);
//        EvaluatorManager ev = new EvaluatorManager(selectExpressionItem.getExpression());
//        determine if is groupby
        if(selectExpressionItem.getExpression() instanceof Function){
            this.isGroupBy =true;
        }
    }

    @Override
    public Iterator iterator() {

        if(this.isGroupBy==true){
            return new AggrNode(this.projectAttrs,this.groupByColumns,leftChildNode.iterator());
        }else {
            return new Itr();
        }
    }

    @Override
    public void forEach(Consumer action) {

    }

    @Override
    public Spliterator spliterator() {
        return null;
    }

//    FIRSTNAME AS A
//    COLUMNNAME = "FIRSTNAME", ALIAS ="A"
//
//    PLAYERS.FIRSTNAME
//    expression
//        |-expression
//              |-columnName:FIRSTNAME
//              |-table:p1
//    COLUMNNAME = "FIRSTNAME",TABLE="PLAYERS"

    private void setSelectAttr(List<SelectItem> selectItems){
        for(SelectItem selectitem:selectItems){
            selectitem.accept(this);

        }
    }

    public Schema getSchema(){
        return this.leftChildNode.getSchema();
    }

    public void upDateSchema(){

    }
}
