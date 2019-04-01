package dubstep.TreeNode;

import jdk.nashorn.api.tree.Tree;
import net.sf.jsqlparser.statement.select.*;
import java.util.*;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

//cross product
public class JoinNode extends TreeNode {
//设置左边的node，右边的node，然后用naive的方法

    public JoinNode(FromItemNode fromItemNode, List<Join> joins) {
//         left
//        this.setLeftChildNode(new FromItemNode(fromItem));
//          right
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
        Iterator<Tuple> lfItr;
        Iterator<Tuple> rgItr;
        Tuple lftuple;

        public Itr() {
            lfItr = JoinNode.this.leftChildNode.iterator();
            rgItr = JoinNode.this.rightChildNode.iterator();
            lftuple= lfItr.next();
        }

        @Override
        public void remove() {

        }

        @Override
        public void forEachRemaining(Consumer action) {

        }

        @Override
        public boolean hasNext() {
            if (rgItr.hasNext()) {
                return true;
            } else {

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
                rgItr = JoinNode.this.rightChildNode.iterator();
                rightTp= rgItr.next();
            }
            returnTp.rawColumns.putAll(leftTp.rawColumns);
            returnTp.rawColumns.putAll(rightTp.rawColumns);
            returnTp.columnValues.putAll(leftTp.columnValues);
            returnTp.columnValues.putAll(rightTp.columnValues);
            returnTp.coldefinitions.addAll(leftTp.coldefinitions);
            returnTp.coldefinitions.addAll(rightTp.coldefinitions); 
            return returnTp;
        }
    }



//
//    }
//

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
