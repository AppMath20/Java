package laboratoryManager;

import ru.spbstu.pipeline.IExecutor;
import ru.spbstu.pipeline.IReader;
import ru.spbstu.pipeline.IWriter;
import ru.spbstu.pipeline.RC;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class AnalyzerGrammar {

    @FunctionalInterface
    interface Rule {
        RC apply(Object obj);
    }

    private enum Grammar {

        INPUT_FILE("INPUT_FILE"),
        OUTPUT_FILE("OUTPUT_FILE"),
        READER("READER"),
        EXECUTOR("EXECUTOR"),
        WRITER("WRITER");

        Grammar(String nameInConfig) {
            this.nameInConfig = nameInConfig;
        }
        public final String nameInConfig;
    }

    private final Grammar param;
    private final Rule code;
    static String inputFile;
    static String outputFile;
    public static IWriter mWriter;
    public static IReader mReader;
    public static IExecutor [] mExecutor;

    AnalyzerGrammar(Grammar param, Rule code) {
        this.param = param;
        this.code = code;
    }
    public static String getParam(Grammar param){
        return param.nameInConfig;
    }

    public static AnalyzerGrammar[] getRules() {

        AnalyzerGrammar[] Rules =  {

                new AnalyzerGrammar(Grammar.INPUT_FILE, paramVal -> {
                    inputFile = (String) paramVal;
                    return RC.CODE_SUCCESS;
                }),

                new AnalyzerGrammar(Grammar.OUTPUT_FILE, paramVal -> {
                    outputFile = (String) paramVal;
                    return RC.CODE_SUCCESS;
                }),

                new AnalyzerGrammar(Grammar.READER, paramVal -> {
                    Map<String,IReader> readerMap = (Map<String,IReader>) paramVal;
                    String configFile = null;
                    for(Map.Entry<String,IReader> item : readerMap.entrySet()) {
                        mReader = item.getValue();
                        configFile = item.getKey();
                    }
                    assert mReader != null;
                    return mReader.setConfig(configFile);
                }),

                new AnalyzerGrammar(Grammar.EXECUTOR, paramVal -> {
                    Map<String,IExecutor> [] executorMaps = (Map<String,IExecutor>[]) paramVal;
                    mExecutor = new IExecutor[executorMaps.length];
                    String configFile = null;
                    for (int i = 0; i < executorMaps.length; i++) {
                        Map<String,IExecutor> executorMap = executorMaps[i];
                        for(Map.Entry<String,IExecutor> item : executorMap.entrySet()) {
                            mExecutor[i] = item.getValue();
                            configFile = item.getKey();
                        }
                        RC code = mExecutor[i].setConfig(configFile);
                        if (code != RC.CODE_SUCCESS)
                            return code; }
                    return RC.CODE_SUCCESS;
                }),

                new AnalyzerGrammar(Grammar.WRITER, paramVal -> {
                    Map<String,IWriter> writerMap = (Map<String,IWriter>) paramVal;
                    mWriter = null ;String configFile = null;
                    for(Map.Entry<String,IWriter> item : writerMap.entrySet()) {
                        mWriter = item.getValue();
                        configFile = item.getKey();
                    }
                    assert mWriter != null;
                    return mWriter.setConfig(configFile);
                })
        };
        return Rules;
    }

    public static RC analyzing(FileInputStream configStream, Logger logger) {
        AnalyzerGrammar[] Rules = getRules();
        String[] Lexeme = new String[Rules.length];

        for(int i = 0; i < Rules.length; i++){
            Lexeme[i] = Rules[i].param.nameInConfig;
        }

        ParserSyntax parserSyntax = ParserSyntax.CreateParser(Lexeme, configStream, logger);

        if (parserSyntax == null)
            return RC.CODE_CONFIG_GRAMMAR_ERROR;

        for (AnalyzerGrammar item : Rules) {
            Grammar localParam = item.param;
            Rule localCode = item.code;
            ArrayList<String> paramValAsString = parserSyntax.getParameter(localParam.nameInConfig);
            Object paramValue = ParserSemantics.SemanticParsing(paramValAsString, localParam.nameInConfig, logger);

            if (paramValue == null)
                return RC.CODE_CONFIG_SEMANTIC_ERROR;
            RC code = localCode.apply(paramValue);
            if (code != RC.CODE_SUCCESS)
                return code;
        }
        return RC.CODE_SUCCESS;
    }
}
