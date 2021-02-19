package laboratoryManager;

import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {

        if(args == null || args.length == 0) {
            System.out.println("Error: There is no command-line arguments");
            return;
        }
        Logger logger = Logger.getLogger("Manager pipeline");
        String configFile = args[0];

        Manager m_manager = new Manager(configFile,logger);
        m_manager.createPipeline();

        if(m_manager.isReadyToStart()){
            m_manager.Start();
        }
    }
}


