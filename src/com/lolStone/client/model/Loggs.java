package com.lolStone.client.model;

import com.lolStone.client.enums.LogLevel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by root on 03.03.16.
 */
public class Loggs {

    Logger log = LogManager.getLogger("Camera Log");

    public void info(String msg)
    {
        log.info(msg);
    }

    public void info(LogLevel level,String msg)
    {
        switch (level)
        {
            case ERROR:
                log.log(Level.ERROR,msg);
                break;
            case INFO:
                log.log(Level.INFO,msg);
                break;
            case FATAL:
                log.log(Level.FATAL,msg);
                break;
        }
    }

    public void info(Exception e)
    {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        info(LogLevel.ERROR, errors.toString());
    }
}
