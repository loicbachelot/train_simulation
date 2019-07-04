package data;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.abs;

/**
 * Created by Adrien on 20/01/2017.
 */
public class Station {
    private String nameStation;
    private int position;
    private ArrayList<String> trainTypeList = new ArrayList<>();
    private boolean terminus;
    private boolean endBranch;
    private HashMap<Train, Double> timeTable = new HashMap<>();
    private int order;
    private int globalOrder;

    /**
     * Station constructor
     *
     * @param nameStation the name of the station
     * @param position    the position of the station
     * @param terminus    boolean used to know if the station is a terminus
     * @param order       integer used to know the order of the station on a line
     */
    public Station(String nameStation, int position, boolean terminus, int order, boolean endBranch, int globalOrder) {
        this.nameStation = nameStation;
        this.position = position;
        this.terminus = terminus;
        this.order = order;
        this.endBranch = endBranch;
        this.globalOrder = globalOrder;
    }

    /**
     * Function that calculates distance between two stations
     *
     * @param currentStation is the current station
     * @param nextStation    is the next station
     * @return Distance between two stations
     */
    public int distanceBetween(Station currentStation, Station nextStation) {
        int current = currentStation.getPosition();
        int next = nextStation.getPosition();
        return abs(current - next);
    }

    /**
     * @return the name of the station
     */
    public String getNameStation() {
        return nameStation;
    }

    /**
     * @return the position of a station
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return the list of train's types
     */
    public ArrayList<String> getTrainType() {
        return trainTypeList;
    }

    /**
     * @return if the station is a terminus
     */
    public boolean isTerminus() {
        return terminus;
    }

    /**
     * @param trainType to add in the list of train's types
     */
    public void addTrainType(String trainType) {
        trainTypeList.add(trainType);
    }

    /**
     * @return the timetable of the station
     */
    public HashMap<Train, Double> getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(Train train) {
        timeTable.remove(train);
    }

    /**
     * @return TODO already this met
     */
    public ArrayList<String> getTrainTypeList() {
        return trainTypeList;
    }

    /**
     * @return return the number of the station
     */
    public int getOrder() {
        return order;
    }

    /**
     * @param train is the next train comming to the station
     */
    public void initTimeTable(Train train) {
        double timeArrived;
        double distance;
        double coef = 20.0; //1unit per 50 ms so 1/0.05 in seconds
        double timeSpentStopped;
        if (train.isWay()) {
            distance = train.getPosition() - this.getPosition();
            timeSpentStopped = train.getTimeSpentStopped(train.getDepartureStation(), this, train.isWay());
        } else {
            distance = this.getPosition() - train.getPosition();
            timeSpentStopped = train.getTimeSpentStopped(train.getDepartureStation(), this, train.isWay());
        }
        timeArrived = (distance / coef) + timeSpentStopped;
        timeTable.put(train, timeArrived);
    }

    /**
     * @param train is the next train comming to the station
     */
    public void refreshTimeTable(Train train, Station departure, long timeToAdd) {
        double timeArrived;
        double distance;
        double coef = 20.0; //1unit per 50 ms so 1/0.05 in seconds
        double timeSpentStopped;
        if (train.isWay()) {
            distance = train.getPosition() - this.getPosition();
            timeSpentStopped = train.getTimeSpentStopped(departure.getNameStation(), this, train.isWay());
        } else {
            distance = this.getPosition() - train.getPosition();
            timeSpentStopped = train.getTimeSpentStopped(departure.getNameStation(), this, train.isWay());
        }
        timeArrived = (distance / coef) + timeSpentStopped;
        timeArrived += timeToAdd;
        timeArrived += 2;
        timeTable.put(train, timeArrived);
    }

    public boolean isEndBranch(boolean way) {
        if (way && order == 0) {
            return endBranch;
        } else if (!way && order != 0) {
            return endBranch;
        } else {
            return false;
        }
    }

    public int getGlobalOrder() {
        return globalOrder;
    }
}
