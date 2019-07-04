package test;

import data.Timer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by william on 23/01/17.
 */
public class TimerTest {
    Timer time;

    @Before
    public void setUp() throws Exception {
        time = new Timer();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void restart() throws Exception {


        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
        }
        time.restart();
        assertEquals("time should be ", 0, time.getSeconds());
    }

    /**
     * Tests the timer for 5 seconds
     *
     * @throws Exception all types of exceptions
     * @see Timer
     */
    @Test
    public void defile5secs() throws Exception {

        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
        }
        assertEquals("time should be ", 5, time.getSeconds());

    }

    @Test
    public void defile5secs2() throws Exception {
        time.setSpeed(2);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            System.out.println(time);
        }
        assertEquals("time should be ", 20, time.getSeconds());

    }
}