package Logger;

import org.apache.log4j.Logger;

public class Log {

    private static final Logger LOGGER = Logger.getLogger(Log.class);

    public static void info(String info){
        LOGGER.info(info);
    }

    public static void error(String info, Exception e){
        LOGGER.error(info + "\n" + e.toString());
    }

}
