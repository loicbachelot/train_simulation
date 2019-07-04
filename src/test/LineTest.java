package test;

import data.Branch;
import data.Line;
import data.Station;
import data.Train;
import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by william on 30/01/17.
 */
public class LineTest {

    private Branch branch;
    private String idBranch = "abc";
    private Line line;
    private char idLine = 1;
    private Train train;
    private int idTrain = 1;
    private String trainType = "TrainType";
    private Date departureTime = DateUtils.addSeconds(new Date(), -5000);
    private boolean way = true;
    private String departureStation = "Cergy";
    private Station station;
    private String nameStation = "nameStation";
    private int position = 40;
    private boolean terminus = false;
    private int order = 3;
    private boolean endBranch = false;

    @Before
    public void setUp() throws Exception {
        /*branch = new Branch(idBranch);
        line = Line.getInstance();
        train = new Train(branch, idTrain, trainType, departureTime, way, departureStation);
        station = new Station(nameStation, position, terminus, order, endBranch,);*/
    }

    @After
    public void tearDown() throws Exception {
        branch = null;
        line = null;
        train = null;
        station = null;
    }

    @Test
    public void addBranch() throws Exception {
        line.addBranch(branch);
        assertEquals("Branch has not been added", line.getBranch(idBranch), branch);
    }

    @Test
    public void addTrain() throws Exception {
        line.addBranch(branch);
        line.addTrain(idBranch, train);
        assertEquals("Train has not been added", line.getTrainToGO(idBranch, new Date()), train);
    }

    @Test
    public void addStation() throws Exception {
        line.addBranch(branch);
        line.addStation(idBranch, station);
        assertEquals("Station has not been added", line.getBranch(idBranch).getStation(nameStation), station);
    }

    @Test
    public void numberDeparturesTrainByBranch() throws Exception {
        line.addBranch(branch);
        line.addStation(idBranch, station);
        line.addTrain(idBranch, train);
        assertEquals("The number of departures train is false", line.numberDeparturesTrainByBranch(idBranch, new Date()), 1);
    }

    @Test
    public void getTrainToGO() throws Exception {
        line.addBranch(branch);
        line.addStation(idBranch, station);
        line.addTrain(idBranch, train);
        assertEquals("The number of departures train is false", line.getTrainToGO(idBranch, new Date()), train);
    }

}