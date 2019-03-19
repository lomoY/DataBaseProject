package dubstep.TreeNode;

import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.List;

public class Schema {

    List<ColumnDefinition> columnDefinitions;

    public Schema(CreateTable createTable) {
        this.columnDefinitions = createTable.getColumnDefinitions();
    }
}
