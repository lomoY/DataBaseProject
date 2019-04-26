package dubstep.Manager;

import dubstep.TreeNode.*;
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
        if(plainSelect.getLimit()!=null){

            LimitOperator lo = new LimitOperator(plainSelect.getLimit());

            this.setLeftChildNode(lo);
            if(plainSelect.getOrderByElements()!=null){

                ProjectionNode projectionNode = new ProjectionNode(plainSelect);
                OrderByOperator odb = new OrderByOperator(plainSelect.getOrderByElements());

                odb.setLeftChildNode(projectionNode);
                lo.setLeftChildNode(odb);
            }

        }else if(plainSelect.getOrderByElements()!=null){
            OrderByOperator odb = new OrderByOperator(plainSelect.getOrderByElements());
            ProjectionNode projectionNode = new ProjectionNode(plainSelect);
            odb.setLeftChildNode(projectionNode);
            this.setLeftChildNode(odb);

        }else {
            ProjectionNode projectionNode = new ProjectionNode(plainSelect);
            this.setLeftChildNode(projectionNode);

        }


    }

    @Override
    public void visit(Union union) {}

    private class Itr implements Iterator<Tuple>{
        Iterator<Tuple> lfItr;

        Itr(){

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
