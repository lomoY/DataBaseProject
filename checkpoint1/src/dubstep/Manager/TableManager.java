package dubstep.Manager;

import dubstep.TreeNode.IndexNode;
import dubstep.TreeNode.Schema;
import dubstep.TreeNode.TableNode;
//import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.util.*;

import net.sf.jsqlparser.statement.create.table.Index;
/**
 * @author Lomo_Y
 */

public class TableManager {

    static  Map<String, TableNode> TableMap = new HashMap<>();
    static Map<String,Schema> SchemaMap = new HashMap<>();
    static Map<String,IndexNode> IndexMap = new HashMap<>();
    static boolean createTable(CreateTable createTable) {

//      Store Table and corresponding Schema
        String tableName = createTable.getTable().getName();
        TableNode newTable = new TableNode(createTable);
        Schema newSchema  = new Schema(createTable);
        IndexNode newIndex= new IndexNode(createTable);
        TableMap.put(tableName,newTable);
        SchemaMap.put(tableName,newSchema);
        IndexMap.put(tableName,newIndex);

//ALIIES Name
        return true;
    }

    public static Map<String, TableNode> getTableMap() {
        return TableMap;
    }

    public static TableNode getTable(String tableName){
        return TableMap.get(tableName);
    }

    public static Schema getSchema(String tableName) {
        return SchemaMap.get(tableName);
    }

    public static IndexNode getIndex(String tableName) { return IndexMap.get(tableName);}
}

