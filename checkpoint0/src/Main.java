package dubstep;
import java.sql.SQLException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;


public class Main{
    public static void main(String arg[]) throws SQLException, ParseException {
            CCJSqlParser parser = new CCJSqlParser(System.in);

        Statement statement = null;
        statement = parser.Statement();

        while(statement !=null){
            if(statement instanceof Select){
                Select select = (Select)statement;
                SelectBody selectBody = select.getSelectBody();
                PlainSelect plainselect = (PlainSelect)selectBody;
                Table tableobj = (Table)plainselect.getFromItem();
                String tableName = tableobj.getName();
                System.out.println(tableName);

            }else if (statement instanceof Delete){
                Delete delete = (Delete)statement;
            }else if (statement instanceof Drop){
                Drop drop = (Drop)statement;
            }else if(statement instanceof Insert){

            }else if (statement instanceof Replace){

            }else if (statement instanceof Truncate){

            }else if (statement instanceof Update){

            }else{
                throw new SQLException("Can't handle: "+ statement);
            }

                statement = parser.Statement();
        }
    }
}