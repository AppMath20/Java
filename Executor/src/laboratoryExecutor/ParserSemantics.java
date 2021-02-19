package laboratoryExecutor;

import ru.spbstu.pipeline.IExecutor;
import ru.spbstu.pipeline.IWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

public class ParserSemantics {

    final static String slashForSemantic = ":";
    private static Object parameter;

    enum Semantic{
        INPUT_FILE,OUTPUT_FILE,BUFFER_SIZE,READER,WRITER,EXECUTOR,SHIFT
    }

    interface CommandInterface {
        Object Command();
    }

    private static final Map<String,CommandInterface> commandHashMap = new HashMap<>();

    static void Initialize(List<String> parameters, Logger logger) {

        commandHashMap.put("READER", new CommandInterface() {
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
                    System.out.println("Error: Not possible to build the corresponding class");
                }
                Object[] result = new Object[2];
                result[0] = writer;
                result[1] = configFilename;
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
                    System.out.println("Error: Not possible to build the corresponding class");
                }
                Object[] result = new Object[2];
                result[0] = writer;
                result[1] = configFilename;
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
                        System.out.println("Error: Not possible to build the corresponding class");
                    }
                    Object[] temp = new Object[2];
                    temp[0] = executor;
                    temp[1] = configFilename;
                    result[i].add(temp);
                }
                return result;
            }
        });

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

        commandHashMap.put("BUFFER_SIZE", new CommandInterface() {
            @Override
            public Object Command() {
                Integer size = Integer.parseInt(parameters.get(0));
                return  size;
            }
        });

        commandHashMap.put("SHIFTING", new CommandInterface() {
            @Override
            public Object Command() {
                String word = parameters.get(0);
                return word.equals("TRUE");
            }
        });
    }

    public static Object SemanticParsing(List<String> parameters, String parameterSemantic, Logger logger ){

        Initialize(parameters,logger);
        for(Map.Entry<String,CommandInterface> item : commandHashMap.entrySet()){
            if(parameterSemantic.equals(item.getKey())) {
                parameter = commandHashMap.get(item.getKey()).Command();
            }
        }
     return parameter;
    }
}
