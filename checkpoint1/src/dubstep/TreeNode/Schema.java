package dubstep.TreeNode;

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
    String tableName;

    public Schema(CreateTable createTable) {
        tableName = createTable.getTable().toString();
        this.columnDefinitions = createTable.getColumnDefinitions();

        List<Index> index = createTable.getIndexes();//return primary key
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


//whenever there is a aliassname the tablename is discarded. thetable will always be referred from the aliasname
//SELECT A FROM R T
//
//public class Cloumn extends ColumnDefinition{
//    columnDefinitions
//    String alias;
//
//}

