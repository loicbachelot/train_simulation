package log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.net.URL;

/**
 * Utility class used to generate Log4j logger.
 *
 * @author Tianxiao.Liu@u-cergy.fr
 */
public class LoggerUtility {

    // private static final String  FILE_LOG_CONFIG = "src/log/log4j-file.properties";
    private static final URL HTML_LOG_CONFIG = LoggerUtility.class.getResource("log4j-html.properties");

    public static Logger getLogger(Class<?> logClass) {
        PropertyConfigurator.configure(HTML_LOG_CONFIG);
        String className = logClass.getName();
        return Logger.getLogger(className);
    }
}
