package dubstep.TreeNode;

import dubstep.Manager.EvaluatorManager;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import java.beans.Expression;
import java.util.*;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 *
 *   @author Lomo Yang
 *   @date May 4th,2019
 *   Convert SelectionNode and JoinNode to EquiJoinNode
 *
 * */

//cross product
public class EquiJoinNode extends TreeNode {

    PrimitiveValue equiValue;
    Expression whereExpression;
    Column leftCol;
    Column rightCol;

    public EquiJoinNode(Expression _whereExpression, Column _leftCol, Column _rightCol){

        this.whereExpression=_whereExpression;
        this.rightCol=_rightCol;
        this.leftCol=_leftCol;

    }

    public EquiJoinNode(FromItemNode fromItemNode, List<Join> joins) {

        FromItem Fi = joins.get(0).getRightItem();
        FromItemNode Fin= new FromItemNode(Fi);
        joins.remove(0);
        if(joins.size()>0){
            JoinNode jd = new JoinNode(Fin, joins);
            this.setRightChildNode(jd);
        }
        else
        {
            this.setRightChildNode(Fin);
        }
        this.setLeftChildNode(fromItemNode);
    }


    private class Itr implements Iterator<Tuple> {

        HashMap<String,List<Tuple>> LhsTable = new HashMap<>();

        Iterator<Tuple> lfItr;
        Iterator<Tuple> rgItr;
        Tuple lftuple;

        public Itr() {

            EvaluatorManager em = new EvaluatorManager();
            lfItr = EquiJoinNode.this.leftChildNode.iterator();
            rgItr = EquiJoinNode.this.rightChildNode.iterator();

            //Init put all left tuple in to corresponding hashtable based on the selection value

            while(lfItr.hasNext()){

                //based on the columnValue;

                Tuple lfTp = lfItr.next();

                //todo
                String key = lfTp.getColumnValue(EquiJoinNode.this.whereExpression.getMethodName()).toRawString();

                if(LhsTable.containsKey(key)){

                    List temp = LhsTable.get(key);
                    temp.add(lfTp);
                    LhsTable.put(key,temp);

                }else{

                    List temp = new ArrayList();
                    temp.add(lfTp);
                    LhsTable.put(key,temp);

                }

            }

            //todo
            Iterator tableItr = LhsTable.get(1).iterator();

            if(lfItr.hasNext()){
                lftuple= lfItr.next();
            }
        }

        @Override
        public void remove() {

        }

        @Override
        public void forEachRemaining(Consumer action) {

        }

        @Override
        public boolean hasNext() {

            if (lfItr.hasNext()) {
                return true;
            } else if(!lfItr.hasNext()){

                if(lftuple!=null&&rgItr.hasNext()==true){
                    return true;
                }else{
                    return false;
                }
            }else {

                return false;
            }
        }

        @Override
        public Tuple next() {

            Tuple leftTp;
            Tuple rightTp;
            Tuple returnTp = new Tuple();

            if (rgItr.hasNext()) {
                leftTp = this.lftuple;
                rightTp= rgItr.next();
            }
            else
            {
                this.lftuple= lfItr.next();
                leftTp = this.lftuple;
                rgItr = EquiJoinNode.this.rightChildNode.iterator();
                rightTp= rgItr.next();
            }
            returnTp.columnList.addAll(leftTp.columnList);
            returnTp.columnList.addAll(rightTp.columnList);
            returnTp.columnValues.putAll(leftTp.columnValues);
            returnTp.columnValues.putAll(rightTp.columnValues);
            return returnTp;
        }
    }

    @Override
    public void forEach(Consumer<? super Tuple> action) {

    }

    @Override
    public Spliterator<Tuple> spliterator() {
        return null;
    }

    @Override
    public Iterator<Tuple> iterator() {
        return new Itr();
    }
}