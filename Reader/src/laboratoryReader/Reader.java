package laboratoryReader;

import ru.spbstu.pipeline.IExecutable;
import ru.spbstu.pipeline.RC;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import static laboratoryReader.AnalyzerGrammar.bufferSize;

public class Reader {

    private IExecutable consumer;
    private BufferedInputStream bis;
    private final Logger logger;

    public Reader(Logger log) {
        logger = log;
    }

    public RC setInputStream(FileInputStream fileInputStream)
    {
        bis = new BufferedInputStream(fileInputStream);
        return RC.CODE_SUCCESS;
    }

    public RC execute(byte[] b)
    {
        try
        {
            int bytesRead = 0;
            while (true)
            {
                byte[] buffer = new byte[bufferSize];
                bytesRead = bis.read(buffer, 0, bufferSize);

                if (bytesRead == -1)
                    return RC.CODE_SUCCESS;

                if (bytesRead != buffer.length)
                    buffer = Arrays.copyOfRange(buffer, 0, bytesRead);

                RC code = consumer.execute(buffer);
                if (code != RC.CODE_SUCCESS)
                    return code;
            }
        }
        catch (IOException e) {
            System.out.println("Error: Unable to read the configuration file");
            return RC.CODE_FAILED_TO_READ;
        }
    }

    public RC setConsumer(IExecutable cons) {
        if(cons == null){
            System.out.println("Error: Not find consumer");
            return RC.CODE_INVALID_ARGUMENT;
        }
        consumer = cons;
        return RC.CODE_SUCCESS;
    }

    public RC setProducer(IExecutable prod){
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
}