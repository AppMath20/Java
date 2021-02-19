package laboratoryReader;

import laboratoryReader.ParserSemantics.Semantic;
import ru.spbstu.pipeline.RC;
import java.io.FileInputStream;
import java.util.List;
import java.util.logging.Logger;

public class AnalyzerGrammar {

    @FunctionalInterface
    interface Rule {
        RC apply(Object obj);
    }

    private enum Grammar {

        BUFFER_SIZE("BUFFER_SIZE", Semantic.BUFFER_SIZE);

        Grammar(String nameInConfig, Semantic semantic) {
            this.nameInConfig = nameInConfig;
            this.semantic = semantic;
        }

        public final String nameInConfig;
        public final Semantic semantic;
    }

    private final Grammar param;
    private final Rule code;
    static int bufferSize;

    AnalyzerGrammar(Grammar param, Rule code) {
        this.param = param;
        this.code = code;
    }

    public static AnalyzerGrammar[] getRules() {
        AnalyzerGrammar[] Rules = new AnalyzerGrammar[]{new AnalyzerGrammar(Grammar.BUFFER_SIZE, paramVal -> {
                bufferSize = (Integer)paramVal;
            return RC.CODE_SUCCESS;
        })};
        return Rules;
    }

    public static RC analyzing(FileInputStream configStream, Logger logger) {
        AnalyzerGrammar[] Rules = getRules();
        ParserSyntax parserSyntax = ParserSyntax.CreateParser(Rules, configStream, logger);

        if (parserSyntax == null)
            return RC.CODE_CONFIG_GRAMMAR_ERROR;

        for (AnalyzerGrammar item : Rules) {
            Grammar localParam = item.param;
            Rule localCode = item.code;
            List<String> paramValAsString = parserSyntax.getParameter(localParam.nameInConfig);
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
