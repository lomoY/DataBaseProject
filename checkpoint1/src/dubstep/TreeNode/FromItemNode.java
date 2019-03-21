package dubstep.TreeNode;

import dubstep.Manager.ProjectionTypeNode;
import dubstep.Manager.TableManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

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


    public FromItemNode(FromItem fromItem) {
        fromItem.accept(this);
        //ALIES = P1
        String alies = fromItem.getAlias();
    }

    private class Itr implements Iterator<Tuple>{
        Iterator<Tuple> lfItr;

        public Itr() {
            lfItr = FromItemNode.this.leftChildNode.iterator();
        }

        @Override
        public void remove() {

        }

        @Override
        public void forEachRemaining(Consumer action) {

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
            Tuple tp = this.lfItr.next();
            return tp;
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


//   todo table结点之后都从这里获取

//    FromItem Visitor

//    这里就是进来一个table,ALIES = p1
    @Override
    public void visit(Table table) {
        String tableName = table.getName();//依旧只返回tablename,不会返回alies
//       todo update tablename to alies if any
        this.leftChildNode= TableManager.getTable(tableName);
    }

    @Override
    public void visit(SubJoin subJoin) {

    }

    //SELECT FIRSTNAME, LASTNAME, WEIGHT, BIRTHDATE FROM (SELECT FIRSTNAME, LASTNAME,FIRSTSEASON, WEIGHT, BIRTHDATE FROM PLAYERS);
//    中的SELECT FIRSTNAME, LASTNAME,FIRSTSEASON, WEIGHT, BIRTHDATE FROM PLAYERS
//    subselect = select
    @Override
    public void visit(SubSelect subSelect) {
            this.leftChildNode = new ProjectionTypeNode(subSelect.getSelectBody());
    }
}
