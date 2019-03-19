package dubstep;

import dubstep.Manager.StatementManager;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;

public class Main {

    public static void main(String[] args) throws ParseException{

        System.out.print("$>");
        CCJSqlParser parser = new CCJSqlParser(System.in);
        Statement statement;
        while ((statement = parser.Statement())!=null) {
            statement.accept(new StatementManager());
            System.out.print("$>");
        }
    }
}
