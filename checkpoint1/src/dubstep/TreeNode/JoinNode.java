package dubstep.TreeNode;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

//cross product
public class JoinNode extends TreeNode {
//设置左边的node，右边的node，然后用naive的方法


//    private class Itr implements Iterator<Tuple>{
//        TreeNode leftChild;
//        TreeNode rightChild;
//
//        @Override
//        public Tuple next() {
//
////            leftChild.iterator().next()
////                    iterator tuple from rightChild:
////                        return<r s>
//
////            return Tuple;
//        }
//
//
//        @Override
//        public void remove() {
//
//        }
//
//        @Override
//        public void forEachRemaining(Consumer<? super Tuple> action) {
//
//        }
//
//        @Override
//        public boolean hasNext() {
//            return false;
//        }
//
//
//    }
//
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