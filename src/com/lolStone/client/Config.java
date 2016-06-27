package com.lolStone.client;

import com.lolStone.client.model.Loggs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by root on 02.03.16.
 */
public class Config {
    static Loggs _log = new Loggs();
    public static String HOST;
    public static int PORT;
    public static int MAX_TRY_RECONNECT;
    public static int TIME_DELEY_RECCONECT;

    public static void loadConfig()
    {
        LoggerContext context = LogManager.getContext(false);
        File file = new File("config/log4j2.xml");
        context.setConfigLocation(file.toURI());
        loadServerConfig();
    }

    /**
     * Load Configs
     */
    private static void loadServerConfig()
    {
        try {
            FileInputStream propFile = new FileInputStream("config/server.properties");
            Properties p = new Properties(System.getProperties());
            p.load(propFile);
            HOST = p.getProperty("host", "127.0.0.1");
            PORT = Integer.parseInt(p.getProperty("port", "7777"));
            MAX_TRY_RECONNECT = Integer.parseInt(p.getProperty("maxCount", "30"));
            TIME_DELEY_RECCONECT = Integer.parseInt(p.getProperty("delay", "30000"));
        }catch (Exception e)
        {
            _log.info(e);
        }
    }
}
