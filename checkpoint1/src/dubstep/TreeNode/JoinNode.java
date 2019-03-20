package dubstep.TreeNode;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class JoinNode extends TreeNode {
//设置左边的node，右边的node，然后用naive的方法


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
