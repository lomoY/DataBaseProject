package dubstep.Manager;

import dubstep.TreeNode.ProjectionNode;
import dubstep.TreeNode.TableNode;
import dubstep.TreeNode.TreeNode;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lomo Yang
 * Description:
 *  TreeManager will generate the parse tree recursively. An iterable instance will be return at the
 *  end
 */

public class TreeManager  implements SelectVisitor,FromItemVisitor{

    static TreeNode parseTree;

    public TreeManager() {
        super();
    }

    public boolean generateTree(Select select){
        select.getSelectBody().accept(this);
        return true;
    }

    /**
     *
     * @param plainSelect:SELECT FIRSTNAME, LASTNAME, WEIGHT, BIRTHDATE FROM PLAYERS WHERE WEIGHT > 200
     *
     */

    @Override
    public void visit(PlainSelect plainSelect) {
        PlainSelect p = plainSelect;
        List<Join> j=p.getJoins();
        ProjectionNode projectionNode = new ProjectionNode(plainSelect);

//        Table Node done
//        第二个表是以Join的显示出现的

        String tableName = plainSelect.getFromItem().toString();
        TableNode tableNode = TableManager.getTable(tableName);
        parseTree = projectionNode;


    }

    public TreeNode getParseTree(){
        return parseTree;
    }

    @Override
    public void visit(Union union) {

    }


//    FromItem Visitor

    @Override
    public void visit(Table table) {
        String tableName = table.getName();
        this.parseTree=TableManager.getTable(tableName);
    }

    @Override
    public void visit(SubJoin subJoin) {
        Object x = subJoin;
        System.out.println(x);

    }

    @Override
    public void visit(SubSelect subSelect) {
        SelectBody select = subSelect.getSelectBody();

        select.accept(this);

    }

}
