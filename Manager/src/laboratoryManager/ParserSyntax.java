package laboratoryManager;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;


public class ParserSyntax {

    private static final HashMap<String, ArrayList<String>> param = new HashMap<String, ArrayList<String>>();

    static final String splitter = ",";
    public static String[] configurationParam;
    private Logger logger;
    private ArrayList<String> paramTitle = new ArrayList<String>();

    ParserSyntax(String[] configParameter, Logger log) {
        configurationParam   = configParameter;
        logger = log;
    }

    ArrayList<String> getParameter(String parameterName) {
        return param.get(parameterName);
    }

    static ParserSyntax CreateParser(String [] params, FileInputStream configStream, Logger logger) {
        ParserSyntax parser = new ParserSyntax(params, logger);
        if (!parser.readConfig(configStream) || configurationParam == null)
            return null;
        return parser;
    }

    public boolean readConfig(FileInputStream configStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(configStream));
            for (String str = reader.readLine(); str != null; str = reader.readLine()) {
                String[] signs = str.split(splitter);
                if (signs.length == 0) {
                    System.out.println("Error: No correct parameters");
                    return false;
                }
                for (int j = 0; j < signs.length; j++) {
                    String line = signs[j].trim();
                    paramTitle.add(signs[++j].trim());
                    param.put(line, paramTitle);
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("Error: Unable to open the configuration file");
        } catch (IOException e) {
            System.out.println("Error: Unable to read the configuration file");
        }
        return false;
    }
}
