package dubstep.Manager;

import dubstep.TreeNode.TreeNode;
import dubstep.TreeNode.Tuple;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

public class StatementManager implements StatementVisitor {

    TreeNode parseTree;

    @Override
    public void visit(Select select) {

        parseTree = new ProjectionTypeNode(select.getSelectBody());

        Optimizer opt = new Optimizer(parseTree);
        opt.optimize(parseTree);

        for (Tuple tuple : parseTree){

            System.out.println(tuple);

        }
    }

    @Override
    public void visit(Delete delete) {

    }

    @Override
    public void visit(Update update) {

    }

    @Override
    public void visit(Insert insert) {

    }

    @Override
    public void visit(Replace replace) {

    }

    @Override
    public void visit(Drop drop) {

    }

    @Override
    public void visit(Truncate truncate) {

    }

    @Override
    public void visit(CreateTable createTable) {
        TableManager.createTable(createTable);
    }
}
