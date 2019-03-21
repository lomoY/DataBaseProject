package dubstep.TreeNode;

import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.ArrayList;
import java.util.List;

public class Schema {

     List<ColumnDefinition> columnDefinitions=new ArrayList<>();
     List<String> aliasValue;

    String aliasName;//<------- for table
    String tableName; //<------for table
    public Schema(CreateTable createTable) {

        this.columnDefinitions = createTable.getColumnDefinitions();
    }

    public List<ColumnDefinition> getColumnDefinitions(){
        return columnDefinitions;
    }

    public void setAliasValue(String alies){
        this.aliasValue.add(alies);
    }

    public List<String> getAliasValue(){
        return this.aliasValue;
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

