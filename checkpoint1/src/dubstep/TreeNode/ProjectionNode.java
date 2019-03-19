package dubstep.TreeNode;

import dubstep.Manager.TreeManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import  net.sf.jsqlparser.expression.Expression;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Lomo Yang
 */

public class ProjectionNode extends TreeNode implements SelectItemVisitor {
    String TableName;
    Table TableObj;
    List<SelectItem> projectAttrs = new ArrayList<SelectItem>();


    public ProjectionNode(PlainSelect plainSelect) {

        setSelectAttr(plainSelect.getSelectItems());

//        Set SelectionNode as Child
        Expression whereCondition = plainSelect.getWhere();
        if(whereCondition!=null){
            SelectionNode selectionNode=new SelectionNode(whereCondition);
            this.setLeftChildNode(selectionNode);
        }

//        Set TableNode as Child
        FromItem fromItem=plainSelect.getFromItem();
        TreeManager tm2 = new TreeManager();
        fromItem.accept(tm2);
        if(leftChildNode==null){
            this.setLeftChildNode(tm2.getParseTree());
        }else{
            this.leftChildNode.setLeftChildNode(tm2.getParseTree());
        }
    }

    /**
     * Iterator
     *
     * @return
     */

    private class Itr implements Iterator<Tuple> {
        Iterator<Tuple> lfir;
        List<SelectItem> projectAttrs;

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
            tp.upDateColumn(projectAttrs);
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

    }

    @Override
    public void visit(AllTableColumns allTableColumns) {

    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        this.projectAttrs.add(selectExpressionItem);
    }

    private void setSelectAttr(List<SelectItem> selectItems){
        for(SelectItem selectitem:selectItems){
            selectitem.accept(this);
        }
    }













    @Override
    public Iterator iterator() {
        return new Itr();
    }

    @Override
    public void forEach(Consumer action) {

    }

    @Override
    public Spliterator spliterator() {
        return null;
    }
}
