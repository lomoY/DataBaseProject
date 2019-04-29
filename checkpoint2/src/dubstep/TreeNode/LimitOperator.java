package dubstep.TreeNode;

import net.sf.jsqlparser.statement.select.Limit;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class LimitOperator extends TreeNode {
    Limit limit=null;
    int rowCount = 0;

    public LimitOperator(Limit _limit) {
        this.limit=_limit;
        rowCount=(int)this.limit.getRowCount();
    }

    private class Itr implements Iterator<Tuple>{
        Iterator lfItr;

        public Itr() {
            this.lfItr = LimitOperator.this.leftChildNode.iterator();
        }

        @Override
        public void remove() {

        }

        @Override
        public void forEachRemaining(Consumer<? super Tuple> action) {

        }

        @Override
        public boolean hasNext() {

            if(rowCount>0&&lfItr.hasNext()){

                rowCount--;
                return true;

            }else {

                return false;
            }

        }

        @Override
        public Tuple next() {

            return (Tuple) lfItr.next();
        }


    }
    @Override
    public void forEach(Consumer action) {

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
