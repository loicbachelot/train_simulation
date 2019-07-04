package data;

import javafx.beans.property.DoubleProperty;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Created by loic on 21/01/17.
 * this class is a timer, used to synchronize all the different threads
 */
public class Timer extends Thread {
    private final static Logger logger = LoggerUtility.getLogger(Timer.class);
    private static Timer instance;
    private static String separator = ":";
    private Date start;
    private Date checkpoint;
    private long seconds;
    private double speed;
    private int currentSecs;
    private int currentMins;
    private int currentHours;
    private long millis;


    /**
     * Start the time from now
     *
     * @see Date
     */
    public Timer() {
        this.start = new Date();
        checkpoint = start;
        this.seconds = 0;
        this.speed = 1;
        start();
    }

    /**
     * Start timer from given date
     *
     * @param start given date to start
     * @see Date
     */
    public Timer(Date start) {
        this.start = start;
        this.seconds = 0;
        this.speed = 1;
    }

    public static Timer getInstance() {

        if (instance == null) {
            synchronized (Timer.class) {
                instance = new Timer();
            }
        }
        return instance;
    }

    /**
     * restart the timer to the origin
     */
    public void restart() {
        this.start = new Date();
        this.checkpoint = start;
        this.seconds = 0;
    }

    /**
     * @return the seconds time
     */
    public long getSeconds() {
        return seconds;
    }

    /**
     * @return the current time
     */
    public Date getCheckpoint() {
        return checkpoint;
    }

    /**
     * get the seconds seconds truncated value
     *
     * @return int
     */
    private int getCurrentSecs() {
        getTimeDigits();
        return currentSecs;
    }

    /**
     * get the seconds minutes truncated value
     *
     * @return int
     */
    private int getCurrentMins() {
        getTimeDigits();
        return currentMins;
    }

    /**
     * get the seconds hours truncated value
     *
     * @return int
     */
    private int getCurrentHours() {
        getTimeDigits();
        return currentHours;
    }

    /**
     * reload currentTime fields
     */
    private void getTimeDigits() {

        long time = getSeconds();


        int hours = 0;
        int secs = (int) (time % 60);
        int mins = (int) (time / 60);
        if (mins >= 60) {
            hours = (int) (time / 60);
            mins %= 60;
        }


        currentHours = hours;
        currentMins = mins;
        currentSecs = secs;

    }

    /**
     * @param speed new speed level for the timer
     */
    public synchronized void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * @return String timer in HH:MM:SS format
     */
    @Override
    public String toString() {
        return String.format("%02d", getCurrentHours()) + separator + String.format("%02d", getCurrentMins()) + separator + String.format("%02d", getCurrentSecs());
    }

    /**
     * run the timer by increasing by one second * speed each second
     */
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error("Timer error : ", e);
            }
            update();
        }
    }

    /**
     * update of the timer
     */
    private void update() {
        Date tmp = new Date();
        millis += ((tmp.getTime() - checkpoint.getTime()) * speed);
        checkpoint = tmp;


        seconds = millis / 1000;
    }

    /**
     * @return the start date
     */
    public Date getStart() {
        return start;
    }

    public long getMillis() {
        return millis;
    }

    public Date getLast() {
        return (new Date(getMillis()));
    }

    public long getCurrentDate() {
        return getSeconds();
    }

    public void setSpeed(DoubleProperty doubleProperty) {
        this.speed = doubleProperty.doubleValue();
        System.out.println("speed = " + speed);
    }
}
