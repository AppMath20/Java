package laboratoryWriter;

import ru.spbstu.pipeline.IExecutable;
import ru.spbstu.pipeline.RC;

import java.io.*;
import java.util.logging.Logger;

import static laboratoryWriter.AnalyzerGrammar.bufferSize;

public class Writer {

    private IExecutable producer;
    private BufferedOutputStream bos;
    private final Logger logger;

    public Writer(Logger log) {
        logger = log;
    }

    public RC setOutputStream(FileOutputStream fileOutputStream)
    {
        bos = new BufferedOutputStream(fileOutputStream);
        return RC.CODE_SUCCESS;
    }

    public RC execute(byte[] buffer)
    {
        try
        {
            int i = 0;
            while (i*bufferSize + bufferSize <= buffer.length - 1)
            {
                bos.write(buffer, i*bufferSize, bufferSize);
                i += 1;
            }
            bos.write(buffer,i*bufferSize, buffer.length - i*bufferSize);
            bos.flush();
            return RC.CODE_SUCCESS;
        }
        catch (IOException e) {
            System.out.println("Error: Unable to read the configuration file");
            return RC.CODE_FAILED_TO_WRITE;
        }
    }

    public RC setConsumer(IExecutable o) {

        return RC.CODE_SUCCESS;
    }

    public RC setProducer(IExecutable o) {

        producer = o;
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
