package dubstep.Manager;

import dubstep.TreeNode.IndexNode;
import dubstep.TreeNode.Schema;
import dubstep.TreeNode.TableNode;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lomo_Y
 */

public class TableManager {

    static  Map<String, TableNode> TableMap = new HashMap<String, TableNode>();
    static Map<String,Schema> SchemaMap = new HashMap<>();
    static Map<String,IndexNode> IndexMap = new HashMap<>();

    static boolean createTable(CreateTable createTable){

//      Store Table and corresponding Schema
        String tableName = createTable.getTable().getName();
        Schema newSchema  = new Schema(createTable);
        SchemaMap.put(tableName,newSchema);

        TableNode newTable = new TableNode(createTable);
        TableMap.put(tableName,newTable);
        IndexNode newIndex= new IndexNode(createTable);
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
