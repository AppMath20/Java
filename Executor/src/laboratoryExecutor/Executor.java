package laboratoryExecutor;

import ru.spbstu.pipeline.IExecutable;
import ru.spbstu.pipeline.IExecutor;
import ru.spbstu.pipeline.RC;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Logger;
import static laboratoryExecutor.AnalyzerGrammar.possibleShift;

public class Executor implements IExecutor{

    private static  final int sizeLong = 8;
    private IExecutable consumer;
    private IExecutable producer;
    private final Logger logger;

    public Executor(Logger log) {
        logger = log;
    }

    public RC execute(byte[] buffer) {
        if (possibleShift) {
            cyclicShift(buffer);
        }
        return consumer.execute(buffer);
    }

    public RC setConsumer(IExecutable cons) {
        if(cons == null){
            System.out.println("Error: Not find consumer");
            return RC.CODE_INVALID_ARGUMENT;
        }
        consumer = cons;
        return RC.CODE_SUCCESS;
    }

    public RC setProducer(IExecutable prod) {
        if(prod == null){
            System.out.println("Error: Not find producer");
            return RC.CODE_INVALID_ARGUMENT;
        }
        producer = prod;
        return RC.CODE_SUCCESS;
    }

    public RC setConfig(String filename) {
        try {
            FileInputStream configStream = new FileInputStream(filename);
            return AnalyzerGrammar.analyzing(configStream, logger);
        } catch (FileNotFoundException e) {
            System.out.println("Error: Unable to open the configuration file");
            return RC.CODE_INVALID_INPUT_STREAM;
        }
    }

    public void cyclicShift(byte[] buffer) {

        for(int j = 0; j < buffer.length / 2; j += sizeLong ) {
            for (int i = j; i < sizeLong; i++) {
                byte temp = buffer[i];
                buffer[i] = buffer[buffer.length - 1 - i];
                buffer[buffer.length - 1 - i] = temp;
            }
        }
    }
}
