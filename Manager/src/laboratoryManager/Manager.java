package laboratoryManager;

import ru.spbstu.pipeline.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
import static laboratoryManager.AnalyzerGrammar.*;

public class Manager{

    private final String configFile;
    private  final Logger logger;
    private RC code;

    Manager(String configFile, Logger logger)
    {
        this.configFile = configFile;
        this.logger = logger;
    }

    public RC createPipeline(){
        try
        {
            FileInputStream cfgStream = new FileInputStream(configFile);
            code = AnalyzerGrammar.analyzing(cfgStream, logger);
            if (code != RC.CODE_SUCCESS) {
                System.out.println("Error: Code grammar not available");
                return code;
            }

            IPipelineStep[] pipelineSteps = new IPipelineStep[1 + mExecutor.length + 1];
            pipelineSteps[0] = mReader;
            System.arraycopy(mExecutor, 0, pipelineSteps, 1, mExecutor.length);
            pipelineSteps[pipelineSteps.length - 1] = mWriter;
            RC localCode;

            for (int i = 0; i < pipelineSteps.length; i++) {
                IPipelineStep step = pipelineSteps[i];
                if (i > 0)
                {
                    localCode = step.setProducer(pipelineSteps[i - 1]);
                    if (localCode != RC.CODE_SUCCESS)
                        return localCode;
                }
                if (i < pipelineSteps.length - 1)
                {
                    localCode = step.setConsumer(pipelineSteps[i + 1]);
                    if (localCode != RC.CODE_SUCCESS)
                        return localCode;
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Error: Impossible to build pipeline");
            return RC.CODE_FAILED_PIPELINE_CONSTRUCTION;
        }
        return RC.CODE_SUCCESS;
    }

    public RC Start(){
        try
        {
            if(!isReadyToStart()){
                System.out.println("Error: Occurred problems with configuration file");
                return RC.CODE_FAILED_PIPELINE_CONSTRUCTION;
            }
            FileInputStream input = new FileInputStream(inputFile);
            FileOutputStream output = new FileOutputStream(outputFile);

            mReader.setInputStream(input);
            mWriter.setOutputStream(output);

            RC localCode = mReader.execute(null);

            input.close();
            output.close();

            return localCode;
        }
        catch (FileNotFoundException e) {
            System.out.println("Error: Unable to open the configuration file");
            return RC.CODE_INVALID_INPUT_STREAM;
        } catch (IOException e) {
            System.out.println("Error: Unable to read the configuration file");
            return RC.CODE_INVALID_INPUT_STREAM;
        }
    }

    boolean isReadyToStart(){
        if((mReader == null )&&(mWriter == null)&&(mExecutor == null)) {
            return false;
        }else
            return  true;
    }
}
