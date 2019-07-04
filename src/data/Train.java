package data;

import app.MapThread;
import exception.TerminusException;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by mady on 08/01/17.
 */
public class Train extends Thread {
    private final static Logger logger = LoggerUtility.getLogger(Train.class);
    private static int COEFCREDIT = 50;
    private static int STATIONCOST = 40;
    private static int MOVECOST = 1;
    private int idTrain;
    private String trainType;
    private Date departureTime;
    private double departureTimeFromLastStation;
    private Canton currentCanton;
    private Station currentStation;
    private Branch branch;
    private int speed;
    private int position;
    private boolean way;
    private int nextStationPosition;
    private String departureStation;
    private boolean hasArrived;
    private boolean onLine;
    private boolean trainAccident;
    private boolean retire;
    /**
     * Distance per time unit.
     */
    /**
     * @param branch           current branch where the train is
     * @param idTrain          integer that defines the train
     * @param trainType        String that defines the train's type
     * @param departureTime    integer that defines the train's departureTime
     * @param way              Boolean used to know the direction of traffic
     * @param departureStation Train departure station
     * @see Branch
     */
    public Train(Branch branch, int idTrain, String trainType, Date departureTime, boolean way, String departureStation) {
        this.departureStation = departureStation;
        this.branch = branch;
        this.idTrain = idTrain;
        this.trainType = trainType;
        this.departureTime = departureTime;
        this.way = way; // 0 = GO an 1 = BACK
        hasArrived = false;
        onLine = false;
        speed = 1;
        trainAccident = false;
    }

    /**
     * @return the position of the train
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position new position of the train
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * init the train
     */
    public void initTrain() {
        position = branch.getStation(departureStation).getPosition();
        try {
            setCurrentCanton(branch.getDepartureCanton(way));
        } catch (TerminusException e) {
            logger.error(e);
        }
        currentCanton.setTrain(way);
        currentStation = branch.getStation(departureStation);
    }

    /**
     * @param nanoseconde is the train stop time
     */
    public void stopTrain(int nanoseconde) {
        try {
            sleep(nanoseconde);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    /**
     * @return the speed of the train
     */
    public String toString() {
        return "Train [speed=" + speed + "]";
    }

    /**
     * update the position of the train
     */
    private void updatePosition() {
        if (way) {
            position -= speed;
        } else {
            position += speed;
        }
    }

    private void updatePositionGo() {
        position += speed;
    }

    private void updatePositionBack() {
        position -= speed;
    }

    /**
     * @return the current canton
     */
    public Canton getCurrentCanton() {
        return currentCanton;
    }

    /**
     * @param currentCanton new currentCanton
     * @see Canton
     */
    public void setCurrentCanton(Canton currentCanton) {
        this.currentCanton = currentCanton;
    }

    /**
     * @return the train's ID
     */
    public int getIdTrain() {
        return idTrain;
    }

    /**
     * @return the train's type
     */
    public String getTrainType() {
        return trainType;
    }

    /**
     * @return the time when the train start
     */
    public Date getDepartureTime() {
        return departureTime;
    }

    /**
     * @return the time when the train stoped at last station
     */
    public double getDepartureTimeFromLastStation() {
        return departureTimeFromLastStation;
    }

    /**
     * @return the time when the stoped at last station
     */
    public void setDepartureTimeFromLastStation(double departureTimeFromLastStation) {
        this.departureTimeFromLastStation = departureTimeFromLastStation;
    }

    /**
     * @return the current branch
     * @see Branch
     */
    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    /**
     * @return the orientation of the train
     */
    public boolean isWay() {
        return way;
    }

    /**
     * @return the position of the next station
     * @see Station
     */
    public int getNextStationPosition() {
        return nextStationPosition;
    }

    public void setNextStationPosition(int order) {
        this.nextStationPosition = branch.calculateNextStationPosition(order, trainType, way);
    }

    /**
     * @return the station where the train starts
     * @see Station
     */
    public String getDepartureStation() {
        return departureStation;
    }

    /**
     * @return if the train is online
     */
    public boolean isOnLine() {
        return onLine;
    }

    /**
     * @param position is the position of the train
     */
    public void setCurentStationByPosition(int position) {
        currentStation = branch.getStationByPosition(position);
    }

    /**
     * @param onLine is the boolean that indicates if the train is online or not
     */
    public void setIsOnLine(boolean onLine) {
        this.onLine = onLine;
    }

    /**
     * @return the current station
     * @see Station
     */
    public Station getCurrentStation() {
        return currentStation;
    }

    /**
     * @param currentStation is the current station used to set the attribute
     */
    public void setCurrentStation(Station currentStation) {
        this.currentStation = currentStation;
    }

    /**
     * @return the speed of the train
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * @param speed is the setted speed
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * @return if the train is having an accident
     */
    public boolean isTrainAccident() {
        return trainAccident;
    }

    /**
     * @param trainAccident is the boolean setted if a train is having an accident
     */
    public void setTrainAccident(boolean trainAccident) {
        this.trainAccident = trainAccident;
    }

    /**
     * init the time table of the station where the train stops
     */
    public void initAllTimeTable(Train train) {
        ArrayList<Branch> branches = Line.getInstance().calculateAllBranches(trainType);
        ArrayList<Station> allStation = train.getBranch().calculateAllStationLeftToStopWithAllBranches(trainType, train, branches);
        for (Station currentStation : allStation) {
            currentStation.initTimeTable(this);
        }
    }

    /**
     * refresh the time table of the station where the train stops
     */
    public void refreshAllTimeTable(Train train, long timeToAdd) {
        ArrayList<Branch> branches = Line.getInstance().calculateAllBranches(trainType);
        ArrayList<Station> allStation = train.getBranch().calculateAllStationLeftToStopWithAllBranches(trainType, train, branches);
        for (Station currentStation : allStation) {
            currentStation.refreshTimeTable(this, train.currentStation, timeToAdd);
        }
    }

    /**
     * @param station is the station where the train stops
     */
    public double getTimeSpentStopped(String departureStation, Station station, Boolean way) {
        int timeSpent = 0;
        Station departureStationTmp = null;
        ArrayList<Branch> branches = Line.getInstance().calculateAllBranches(trainType);
        for (Branch cB : branches) {
            Collection<Station> allStationTmp = cB.getStations().values();
            for (Station currentStation : allStationTmp) {
                if (currentStation.getNameStation().equals(departureStation)) {
                    departureStationTmp = currentStation;
                }
            }
        }

        ArrayList<Station> allStation = this.getBranch().calculateAllStationLeftToStopWithAllBranches(trainType, this, branches);
        if (!way) {
            for (Station currentStation : allStation) {
                if ((currentStation.getGlobalOrder() < station.getGlobalOrder() && currentStation.getGlobalOrder() > departureStationTmp.getGlobalOrder()) || (currentStation.getGlobalOrder() > station.getGlobalOrder() && currentStation.getGlobalOrder() < departureStationTmp.getGlobalOrder())) {
                    timeSpent++;
                }
            }
            if (timeSpent * 2 > 0)
                return (timeSpent * 2);
            else
                return 0;
        } else {
            for (Station currentStation : allStation) {
                if ((currentStation.getGlobalOrder() > station.getGlobalOrder() && currentStation.getGlobalOrder() < departureStationTmp.getGlobalOrder())) {
                    timeSpent++;
                }
            }
            return timeSpent * 2;
        }
    }

    /**
     * @return the name of the terminus where the train stops
     */
    public String getDestination() {
        ArrayList<Branch> branches = Line.getInstance().calculateAllBranches(trainType);
        ArrayList<Station> allStation = this.branch.calculateAllStationLeftToStopWithAllBranches(trainType, this, branches);
        for (Station currentStation : allStation) {
            if (currentStation.isTerminus())
                return currentStation.getNameStation();
        }
        return null;
    }

    /**
     * new run methode using credits system synchonized with the timer
     */
    @Override
    public void run() {
        Canton nextCanton;
        onLine = true;
        setNextStationPosition(currentStation.getOrder());
        initAllTimeTable(this);
        Date oldTime = Timer.getInstance().getLast();
        int credits = 0;
        currentStation.setTimeTable(this);
        if (Timer.getInstance().getCurrentDate() != 0)
            setDepartureTimeFromLastStation(Timer.getInstance().getCurrentDate());
        while (!hasArrived) {
            if (isTrainAccident()) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                oldTime = Timer.getInstance().getLast();
            } else {
                try {
                    sleep(MapThread.TIME_UNIT_TRAIN); //to slow the thread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                credits = credits + getCredits(oldTime);
                oldTime = Timer.getInstance().getLast();
                while (credits > 0) {
                    if (position == nextStationPosition) {
                        setCurentStationByPosition(position);
                        if (currentStation.isTerminus()) {
                            hasArrived = true;
                            credits = 0;
                            logger.trace(this.getName() + " has arrived successfully");
                            currentCanton.exit(way);
                        } else {
                            credits = credits - STATIONCOST;
                            currentStation.setTimeTable(this);
                            refreshAllTimeTable(this, Timer.getInstance().getCurrentDate());
                            if (currentStation.isEndBranch(way)) {
                                nextStationPosition = 10000; //Temporary solution
                            } else {
                                setNextStationPosition(currentStation.getOrder());
                            }
                        }
                    } else {
                        if (way) {
                            if (nextStationPosition == 10000) {
                                if (position - speed < (currentStation.getPosition() - 50)) {//start of the branch
                                    changeBranch(credits);
                                } else {
                                    updatePositionBack();
                                    credits = credits - MOVECOST;
                                }
                            } else {
                                if (position - speed <= currentCanton.getStartPoint()) {
                                    nextCanton = branch.getCanton(currentCanton.getID() - 1, way);
                                    credits = changeCanton(nextCanton, credits);
                                } else {
                                    updatePositionBack();
                                    credits = credits - MOVECOST;
                                }
                            }
                        } else {
                            if (position + speed > branch.getTotalLength()) {//start of the branch
                                changeBranch(credits);
                            } else {
                                if (position + speed >= currentCanton.getEndPoint()) {
                                    nextCanton = branch.getCanton(currentCanton.getID() + 1, way);
                                    credits = changeCanton(nextCanton, credits);
                                } else {
                                    updatePositionGo();
                                    credits = credits - MOVECOST;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private int getCredits(Date oldTime) {
        return ((int) (Timer.getInstance().getMillis() - oldTime.getTime())) / COEFCREDIT;
    }

    private int changeCanton(Canton nextCanton, int credits) {
        if (nextCanton != null) {
            if (nextCanton.enter(this) == 1) {
                credits = -40;
            } else {
                credits = credits - MOVECOST;
            }
        } else {
            updatePosition();
            credits = credits - MOVECOST;
        }
        return credits;
    }

    private int changeBranch(int credits) {
        Branch tmpBranch;
        for (String branchID : branch.getNextBranch(way)) {
            tmpBranch = Line.getInstance().getBranch(branchID);
            if (tmpBranch.getTrainsTypeList().contains(trainType)) {
                try {
                    if (tmpBranch.getDepartureCanton(way).isFree(way)) {
                        branch = tmpBranch;
                        currentCanton.exit(way);
                        currentCanton = tmpBranch.getDepartureCanton(way);
                        logger.trace("Change branch to " + branch.getIdBranch() + " success");
                        if (way) {
                            setNextStationPosition(branch.getStations().size());
                        } else {
                            setNextStationPosition(-1);
                        }
                        credits = credits - MOVECOST;
                    } else {
                        logger.trace("Change branch to " + branch.getIdBranch() + " fail : canton occupied");
                        credits = -40;
                    }
                } catch (TerminusException e) {
                    e.printStackTrace();
                }
            }
        }
        return credits;
    }

    public void retire() {
        retire = true;
    }

    public boolean isRetire() {
        return retire;
    }
}
