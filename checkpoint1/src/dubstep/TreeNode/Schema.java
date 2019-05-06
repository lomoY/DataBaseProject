package dubstep.TreeNode;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lomo
 */

public class Schema {

    List<ColumnDefinition> columnDefinitions=new ArrayList<>();
    public List<Column> columnList = new ArrayList<>();

    String tableName;

    public Schema(CreateTable createTable) {

        tableName = createTable.getTable().toString();
        this.columnDefinitions = createTable.getColumnDefinitions();

        for(ColumnDefinition cd:createTable.getColumnDefinitions()){

            Column cl = new Column();
            Table tb = new Table();
            tb.setName(this.tableName);
            cl.setTable(tb);
            cl.setColumnName(cd.getColumnName());
            columnList.add(cl);
        }


        List<Index> index = createTable.getIndexes();//return primary key
    }

    public List<Column> getColumnList() {
        return columnList;
    }

    public List<ColumnDefinition> getColumnDefinitions(){
        return columnDefinitions;
    }

    public void reNameColumn(String oldName, String newName){

        for(ColumnDefinition columnDefinition:columnDefinitions){
               if(columnDefinition.getColumnName()==oldName){
                   columnDefinition.setColumnName(newName);
               }
        }
    }


}

