package dubstep.TreeNode;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import java.util.*;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

//cross product
public class JoinNode extends TreeNode {

    List<Column> LhsColumnList = new ArrayList<>();
    List<Column> RhsColumnList = new ArrayList<>();


    public JoinNode(FromItemNode fromItemNode, List<Join> joins) {

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
        this.LhsColumnList=leftChildNode.getLhsColumnList();
        this.RhsColumnList=rightChildNode.getLhsColumnList();
    }


    private class Itr implements Iterator<Tuple> {
        Iterator<Tuple> lfItr;
        Iterator<Tuple> rgItr;
        Tuple LhsTuple;
        Tuple RhsTuple;

        public Itr() {

            lfItr = JoinNode.this.leftChildNode.iterator();
            rgItr = JoinNode.this.rightChildNode.iterator();

        }

        @Override
        public void remove() {

        }

        @Override
        public void forEachRemaining(Consumer action) {

        }

        @Override
        public boolean hasNext() {

            //init phase
            if(this.LhsTuple==null){

                if(lfItr.hasNext()){

                    if(rgItr.hasNext()){

                        this.LhsTuple=lfItr.next();
                        this.RhsTuple=rgItr.next();
                        return true;
                    }else {
                        return false;
                    }

                }else {

                    return false;
                }
            }

            //has left
            else{

                if(rgItr.hasNext()){

                    this.RhsTuple=rgItr.next();
                    return true;

                }else{

                    if(lfItr.hasNext()){

                        this.LhsTuple=lfItr.next();
                        rgItr=JoinNode.this.rightChildNode.iterator();
                        this.RhsTuple=rgItr.next();
                        return true;

                    }else {

                        return false;

                    }
                }

            }
        }

        @Override
        public Tuple next() {


            Tuple returnTp = new Tuple();

            returnTp.columnList.addAll(this.LhsTuple.columnList);
            returnTp.columnList.addAll(this.RhsTuple.columnList);
            returnTp.columnValues.putAll(this.LhsTuple.columnValues);
            returnTp.columnValues.putAll(this.RhsTuple.columnValues);

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