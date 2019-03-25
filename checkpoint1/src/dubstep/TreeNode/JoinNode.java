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

    public JoinNode(FromItem fromItem, List<Join> joins) {
//         left
        this.setLeftChildNode(new FromItemNode(fromItem));
        /**
         * a b c d e
         *
         * a b c d e
         *
         * a, b c d e
         * b, c d e
         * c, d e
         * d, e
         * de
         * cde
         * bcde
         * abcde
         *
         *
         *
         */
//        right
        FromItem Fi = joins.get(0).getRightItem();
        joins.remove(0);
        JoinNode jd = new JoinNode(Fi, joins);
        this.setRightChildNode(jd);
    }


    private class Itr implements Iterator<Tuple> {
        Iterator<Tuple> lfItr;
        Iterator<Tuple> rgItr;

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
            if (lfItr.hasNext()) {
                return true;
            } else {

                return false;
            }
        }

        @Override
        public Tuple next() {
//            Tuple leftTp;
            Tuple rightTp;
            Tuple returnTp = new Tuple();
            Tuple leftTp = lfItr.next();


            while (rgItr.hasNext()) {
//                leftTp = this.lfItr.next();
                rightTp = this.rgItr.next();

                returnTp.rawColumns.putAll(leftTp.rawColumns);
                returnTp.rawColumns.putAll(rightTp.rawColumns);
                returnTp.columnValues.putAll(leftTp.columnValues);
                returnTp.columnValues.putAll(rightTp.columnValues);
                returnTp.coldefinitions.addAll(leftTp.coldefinitions);
                returnTp.coldefinitions.addAll(rightTp.coldefinitions);
                return returnTp;
            }

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
            return null;
        }
    }
