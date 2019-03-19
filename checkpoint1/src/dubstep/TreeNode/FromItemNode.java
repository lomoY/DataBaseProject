package dubstep.TreeNode;

import dubstep.Manager.TableManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author Lomo Yang
 *
 * A Project Operation may project tuples from a Table, or Subselect.
 * The purose of FromItemNode is to return the result of Table or Subselect to
 * the parent node.
 */

public class FromItemNode extends TreeNode implements FromItemVisitor {

    private class Itr implements Iterator{
        @Override
        public void remove() {

        }

        @Override
        public void forEachRemaining(Consumer action) {

        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            return null;
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
        return null;
    }


//   todo table结点之后都从这里获取

//    FromItem Visitor

    @Override
    public void visit(Table table) {
        String tableName = table.getName();
//        this.parseTree= TableManager.getTable(tableName);
    }

    @Override
    public void visit(SubJoin subJoin) {

    }

    @Override
    public void visit(SubSelect subSelect) {
        SelectBody select = subSelect.getSelectBody();

//        select.accept(this);

    }
}
