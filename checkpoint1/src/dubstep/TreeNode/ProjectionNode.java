package dubstep.TreeNode;

import dubstep.Manager.TableManager;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

import javax.lang.model.type.NullType;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Lomo Yang
 */

public class ProjectionNode extends TreeNode implements SelectItemVisitor {
    String FromItemName;
    Table TableObj;
    List<SelectExpressionItem> projectAttrs = new ArrayList<>();
    List<String> renameAttrs = new ArrayList<String>();
    List<Join> joins= new ArrayList<>();


    public ProjectionNode(PlainSelect plainSelect) {

//        set columns that need to be projected

        setSelectAttr(plainSelect.getSelectItems());
//        Set SelectionNode as Child
        Expression whereCondition = plainSelect.getWhere();
        if(whereCondition!=null){
            SelectionNode selectionNode=new SelectionNode(whereCondition);
            this.setLeftChildNode(selectionNode);
        }

        FromItem fromItem=plainSelect.getFromItem();
        FromItemNode fromItemNode = new FromItemNode(fromItem);
        List<Join> joins= plainSelect.getJoins();
        if(joins!= null)
        {
            JoinNode joinNode = new JoinNode(fromItemNode, joins);
            if(leftChildNode== null)
            {
                this.setLeftChildNode(joinNode);
            }
            else
            {
                this.leftChildNode.setLeftChildNode(joinNode);
            }
        }
      else
        {
//            Set TableNode as Child

            if(leftChildNode==null){
                this.setLeftChildNode(fromItemNode);
            }else{
                this.leftChildNode.setLeftChildNode(fromItemNode);
            }

        }
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
            Tuple tp = lfir.next();//FIRSTNAME AS A
//            在update的时候，要把心的schema传进去，所以alies是要在这里获得的
//            在这里要做两件事。1. 选取需要的column；2.更新column的名字
//            问题，应该在哪里更新schema呢？是在next里呢，还是在Itr初始化的时候呢...
            //最好是先通过更新schema，然后再来做

            if(projectAttrs.size()!=0){//if not Players.*
                tp.upDateColumn(projectAttrs);
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
//

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
//        Schema schema = TableManager.getSchema();
        String alias=selectExpressionItem.getAlias();
        this.projectAttrs.add(selectExpressionItem);

        if(alias!=null){
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
}
