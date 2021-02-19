package laboratoryReader;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class ParserSyntax {

    private static final HashMap<String, List<String> > param = new HashMap<String, List<String>>();

    static final String splitter  = ",";
    static final  String slashForComment = "//";
    public static String [] configurationParam;
    private final Logger logger;
    private List<String> paramTitle;

    ParserSyntax(String [] configParameter, Logger log){
        configurationParam = configParameter;
        logger = log;
    }

    List<String> getParameter(String parameterName){
        return param.get(parameterName);
    }

    static ParserSyntax CreateParser(laboratoryReader.AnalyzerGrammar[] params, FileInputStream configStream, Logger logger)
    {
        String[] paramNamesInConfig = new String[params.length];
        for (int i = 0; i<params.length; i++)
            paramNamesInConfig[i] = params[i].toString();//<~~~~~~~~~~~~//
        ParserSyntax parser = new ParserSyntax(paramNamesInConfig, logger);
        if (!parser.readConfig(configStream)|| param == null || configurationParam == null)
            return null;
        return parser;
    }

    public boolean readConfig(FileInputStream configStream) {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(configStream));
            for (String str = reader.readLine(); str != null; str = reader.readLine()) {

                if (str.trim().startsWith(slashForComment)) {
                    continue;
                } else {
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