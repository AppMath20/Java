package laboratoryManager;

import ru.spbstu.pipeline.IExecutor;
import ru.spbstu.pipeline.IReader;
import ru.spbstu.pipeline.IWriter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ParserSemantics {

    final static String slashForSemantic = ";";
    private static Object parameter;

    interface CommandInterface {
        Object Command();
    }

    private static final Map<String,CommandInterface> commandHashMap = new HashMap<>();

    static void Initialize(ArrayList<String> parameters, Logger logger) {

        commandHashMap.put("INPUT_FILE", new CommandInterface() {
            @Override
            public Object Command() {
                String inputFile = parameters.get(0);
                return inputFile;
            }
        });

        commandHashMap.put("OUTPUT_FILE", new CommandInterface() {
            @Override
            public Object Command() {
                String outputFile = parameters.get(0);
                return outputFile;

            }
        });

        commandHashMap.put("READER", new CommandInterface() {
            @Override
            public Object Command() {
                String[] signs = parameters.get(0).split(slashForSemantic);
                String className = signs[0].trim();
                String configFilename = signs[1].trim();
                IReader reader = null;
                try {
                    reader = (IReader) Class.forName(className).getConstructor(Logger.class).newInstance(logger);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                        NoSuchMethodException | ClassNotFoundException e) {
                    System.out.println("Error: Not possible to build class Reader");
                }
                Map<String,IReader> result = new HashMap<>();
                result.put(configFilename,reader);
                return result;
            }
        });

        commandHashMap.put("EXECUTOR", new CommandInterface() {
            @Override
            public Object Command() {
                List<Object> [] result = new ArrayList[parameters.size()];
                for (int i = 0; i<parameters.size(); i++)
                {
                    String[] signs = parameters.get(i).split(slashForSemantic);
                    String configFilename = signs[1].trim();
                    String className = signs[0].trim();
                    IExecutor executor = null;
                    try {
                        executor = (IExecutor) Class.forName(className).getConstructor(Logger.class).newInstance(logger);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                            NoSuchMethodException | ClassNotFoundException e) {
                        System.out.println("Error: Not possible to build class Executor");
                    }
                    Map<String,IExecutor> temp = new HashMap<>();
                    temp.put(configFilename,executor);
                    result[i].add(temp);
                }
                return result;
            }
        });

        commandHashMap.put("WRITER", new CommandInterface() {
            @Override
            public Object Command() {
                String[] signs = parameters.get(0).split(slashForSemantic);
                String configFilename = signs[1].trim();
                String className = signs[0].trim();
                IWriter writer = null;
                try {
                    writer = (IWriter) Class.forName(className).getConstructor(Logger.class).newInstance(logger);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                        NoSuchMethodException | ClassNotFoundException e) {
                    System.out.println("Error: Not possible to build class Writer");
                }
                Map<String,IWriter> result = new HashMap<>();
                result.put(configFilename,writer);
                return result;
            }
        });

        commandHashMap.put("BUFFER_SIZE", new CommandInterface() {
            @Override
            public Object Command() {
                Integer size = Integer.parseInt(parameters.get(0));
                return  size;
            }
        });
    }

    public static Object SemanticParsing(ArrayList<String> parameters, String parameterSemantic, Logger logger ){

        Initialize(parameters,logger);
        for(Map.Entry<String,CommandInterface> item : commandHashMap.entrySet()){
            if(parameterSemantic.equals(item.getKey())) {
                parameter = commandHashMap.get(item.getKey()).Command();
            }
        }
        return parameter;
    }
}
