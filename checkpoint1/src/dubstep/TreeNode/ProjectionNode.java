package dubstep.TreeNode;

import dubstep.Aggregator.GroupByIterator;
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

    public ProjectionNode(PlainSelect plainSelect) {

//        set columns that need to be projected

        setSelectAttr(plainSelect.getSelectItems());
//        Set SelectionNode as Child
        Expression whereCondition = plainSelect.getWhere();
        if(whereCondition!=null){
            SelectionNode selectionNode=new SelectionNode(whereCondition);
            this.setLeftChildNode(selectionNode);
        }

//        Set TableNode as Child
        FromItem fromItem=plainSelect.getFromItem();
        FromItemNode fromItemNode = new FromItemNode(fromItem);
        if(leftChildNode==null){
                this.setLeftChildNode(fromItemNode);
        }else{
            this.leftChildNode.setLeftChildNode(fromItemNode);
        }

//        GroupBy可以看做是project的一种
        this.groupByColumns=plainSelect.getGroupByColumnReferences();
//        GroupByIterator gbn = new GroupByIterator();
//        List<Join> x =plainSelect.getJoins();
//        Join s = new Join();
//        x.get(0).getRightItem();
//        System.out.println(x);
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
            projectEm = new EvaluatorManager(projectAttrs);
        }

        /**
         *
         * @return tuple accroding to the condition in schema(columndifinitions)
         *
         */

        @Override
        public Tuple next() {
            Tuple tp = lfir.next();

            //这里要做groupbyproject
//            if(true){
//
//            }
            tp=projectEm.evaluateProjection(tp);

            //这里是正常的project
            if(projectAttrs.size()!=0){//if not Players.*
//                tp.upDateColumn(projectAttrs);
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
            return new GroupByIterator(this.projectAttrs,this.groupByColumns,leftChildNode.iterator());
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
