package dubstep.TreeNode;

import dubstep.Manager.TableManager;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author Lomo
 * TableNode Contains:
 * 1. TableFile <---- The path to the table file
 * 2. columnDefinition <------ The column's schema of the table
 * 3.
 */

public class TableNode extends TreeNode implements IntoTableVisitor {
    String TableName;
    String aliasValue;
    Table TableObj;
    File TableFile;

    public TableNode(CreateTable createTable) {

        this.TableName=createTable.getTable().getName();
        this.TableObj=createTable.getTable();
        this.TableFile = Paths.get("data",TableName+".csv").toFile();

    }

    private class Itr implements Iterator<Tuple> {

        BufferedReader br;

        Schema schema = TableManager.getSchema(TableNode.this.TableName);

        List<ColumnDefinition> columnDefinitions = schema.getColumnDefinitions();


        public Itr() {

            try {
                br = new BufferedReader(new FileReader(TableNode.this.TableFile));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        /**
         *
         * @return tuple accroding to the condition in schema(columndifinitions)
         *
         */

        @Override
        public Tuple next() {

            try {

                Tuple tp = new Tuple(columnDefinitions);
                String[] columnValues = this.br.readLine().split("\\|");

                for(int i =0;i<columnValues.length;i++){

                    Table tb = TableNode.this.TableObj;
                    if(TableNode.this.aliasValue!=null){
                        tb.setName(aliasValue);
                    }

                    Column column = new Column(TableNode.this.TableObj,columnDefinitions.get(i).getColumnName().toString());
                    String colDataType = columnDefinitions.get(i).getColDataType().toString();
                    switch (colDataType) {
                        case "int":
                            tp.setColumn(column, new LongValue(Long.parseLong(columnValues[i])));
                            break;
                        case "decimal":
                            tp.setColumn(column, new DoubleValue(Double.parseDouble(columnValues[i])));
                            break;
                        case "date":
                            tp.setColumn(column, new DateValue(columnValues[i]));
                            break;
                        case "varchar":
                        case "char":
                        case "string":
                            tp.setColumn(column, new StringValue(columnValues[i]));
                            break;
                        default:
                            tp.setColumn(column, new NullValue());
                    }
                }
                return tp;

            }catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public boolean hasNext() {
            try{
                if(br==null){
                    return false;
                }
                else if(br.ready()){//second
                    return true;
                }
                else {
                    br.close();
                    br = null;
                    return false;
                }
            }catch (IOException e){
                e.printStackTrace();
                return false;
            }
        }


        @Override
        public void remove() {
        }
        @Override
        public void forEachRemaining(Consumer action) {}

    }

    public void setAliasValue(String alias){
        this.aliasValue=alias;
    }
    public String getAliasValue(){
        return this.aliasValue;
    }

    /**
     * IntoTableVisitor
     * @param table
     */

    @Override
    public void visit(Table table) {

    }

    @Override
    public void forEach(Consumer action) {

    }

    @Override
    public Iterator<Tuple> iterator() {
        return new Itr();
    }

    @Override
    public Spliterator spliterator() {
        return null;
    }
}
