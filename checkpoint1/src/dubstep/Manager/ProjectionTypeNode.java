package dubstep.Manager;

import dubstep.TreeNode.ProjectionNode;
import dubstep.TreeNode.TreeNode;
import dubstep.TreeNode.Tuple;
import net.sf.jsqlparser.statement.select.*;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author Lomo Yang
 * Description:
 *  ProjectionTypeNode will generate the parse tree recursively. An iterable instance will be return at the
 *  end
 */

public class ProjectionTypeNode extends TreeNode implements SelectVisitor {

    public ProjectionTypeNode(SelectBody selectBody) {
        selectBody.accept(this);
    }

    /**
     *
     * @param plainSelect:SELECT FIRSTNAME, LASTNAME, WEIGHT, BIRTHDATE FROM PLAYERS WHERE WEIGHT > 200
     *
     */

    @Override
    public void visit(PlainSelect plainSelect) {

        ProjectionNode projectionNode = new ProjectionNode(plainSelect);
        this.setLeftChildNode(projectionNode);
    }

    @Override
    public void visit(Union union) {}

    private class Itr implements Iterator<Tuple>{
        Iterator<Tuple> lfItr;

        Itr(){
            // todo 这里不知道为什么不能直接用this.leftchildNode()
            lfItr = ProjectionTypeNode.this.getLeftChildNode().iterator();
        }

        @Override
        public boolean hasNext() {
            if(lfItr.hasNext()){
                return true;
            }else {
                return false;
            }
        }

        @Override
        public Tuple next() {
            Tuple tp = lfItr.next();
            return tp;
        }

        @Override
        public void remove() {}

        @Override
        public void forEachRemaining(Consumer<? super Tuple> action) {}
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
