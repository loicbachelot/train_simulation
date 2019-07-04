package data;

import exception.TerminusException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mady on 19/01/17.
 *
 * @author mady
 */
public class Branch {
    private String idBranch;
    private HashMap<Integer, Canton> cantons;
    private HashMap<Integer, Station> stations = new HashMap<>();
    private int totalLength;
    private String upBranchId = new String();
    private String downBranchId = new String();
    private String backBranchId = new String();
    private ArrayList<String> trainsType = new ArrayList<>();
    private int startPosition;
    private int yPosition;


    /**
     * @param idBranch String with the name of the branch
     * @see data.Station
     */

    public Branch(String idBranch) {
        this.idBranch = idBranch;
    }

    public Branch(String idBranch, String upBranchId, String downBranchId, String backBranchId) {
        this.idBranch = idBranch;
        this.backBranchId = backBranchId;
        this.downBranchId = downBranchId;
        this.upBranchId = upBranchId;
    }

    /**
     * @param idBranch    String with the name of the branch
     * @param totalLength lenght of the branch
     * @param cantonSize  size of the cantons
     * @version 0.0.1 no calcul to have the total lenght
     */
    public Branch(String idBranch, int totalLength, int cantonSize) {
        this.idBranch = idBranch;
        this.totalLength = totalLength;
        cantons = new HashMap<>();
        generateCantons(cantonSize);
    }

    /**
     * @param cantonSize is the size of the canton in the branch
     * @see Canton
     */
    public void initBranch(int cantonSize) {
        Station previousStation = null;
        for (Station station : stations.values()) {
            if (previousStation != null) {
                totalLength = totalLength + station.distanceBetween(previousStation, station);
            } else {
                totalLength = station.getPosition();
            }
            previousStation = station;
        }
        cantons = new HashMap<>();
        generateCantons(cantonSize);
    }

    /**
     * @param cantonSize define the canton size
     *                   this methode cut the Branch in cantons depending of the define size
     * @see data.Canton
     */
    private void generateCantons(int cantonSize) {
        int generatedLenght = 0;
        int id = 0;
        Canton canton = new Canton(id, generatedLenght, cantonSize, true);
        cantons.put(canton.getID(), canton);
        generatedLenght = generatedLenght + cantonSize;
        id++;
        while (generatedLenght < (totalLength - cantonSize)) {
            canton = new Canton(id, generatedLenght, cantonSize, false);
            cantons.put(canton.getID(), canton);
            generatedLenght = generatedLenght + cantonSize;
            id++;
        }
        canton = new Canton(id, generatedLenght, totalLength - generatedLenght, true);
        cantons.put(canton.getID(), canton);
    }

    public void addTrainType(String train) {
        trainsType.add(train);
    }

    public ArrayList<String> getTrainsTypeList() {
        return trainsType;
    }


    /**
     * @param currentOrder current order of the current station of the train
     * @param trainType    type of the train
     * @param way          way of the train
     * @return int: position of next station
     * @see data.Train
     */
    public int calculateNextStationPosition(int currentOrder, String trainType, Boolean way) {
        boolean tmp = true;
        if (way) {
            currentOrder--;
            while (tmp) {
                ArrayList<String> trainTypestmp = stations.get(currentOrder).getTrainType();
                if (trainTypestmp.contains(trainType)) {
                    tmp = false;
                } else {
                    currentOrder--;
                }
            }
        } else {
            currentOrder++;
            while (tmp) {
                ArrayList<String> trainTypestmp = stations.get(currentOrder).getTrainType();
                if (trainTypestmp.contains(trainType)) {
                    tmp = false;
                } else {
                    currentOrder++;
                }
            }
        }
        return stations.get(currentOrder).getPosition();
    }

    /**
     * @param trainType is the type of the train we need to calculate the station where it stops
     * @return the station where the train has to stop
     * @see Train
     */
    public ArrayList<Station> calculateAllStationToStop(String trainType) {
        ArrayList<Station> allStation = new ArrayList<>();
        ArrayList<String> trainTypestmp;
        int order = 0;
        while (order != stations.size()) {
            trainTypestmp = stations.get(order).getTrainType();
            if (trainTypestmp.contains(trainType)) {
                allStation.add(stations.get(order));
            }
            order++;
        }
        return allStation;
    }


    /**
     * @param trainType is the type of the train we need to calculate the station where it stops
     * @return the station where the train has to stop
     * @see Train
     */
    public ArrayList<Station> calculateAllStationLeftToStopWithAllBranches(String trainType, Train train, ArrayList<Branch> branch) {
        ArrayList<Station> allStation = new ArrayList<Station>();
        for (Branch currentBranch : branch) {
            ArrayList<Station> allStationTmp = calculateAllStationLeftToStop(trainType, train, currentBranch);
            for (Station currentStation : allStationTmp) {
                allStation.add(currentStation);
            }
        }
        return allStation;
    }


    /**
     * @param trainType is the type of the train we need to calculate the station where it stops
     * @return the station where the train has to stop
     * @see Train
     */
    public ArrayList<Station> calculateAllStationLeftToStop(String trainType, Train train, Branch branch) {
        ArrayList<Station> allStation = new ArrayList<Station>();
        int order = 0;
        int index = 0;
        while (order != branch.stations.size()) {
            ArrayList<String> trainTypestmp = branch.stations.get(order).getTrainType();
            if (trainTypestmp.contains(trainType)) {
                if ((train.isWay() && branch.stations.get(order).getPosition() < train.getPosition())) {
                    allStation.add(order, branch.stations.get(order));
                } else if (!train.isWay() && branch.stations.get(order).getPosition() > train.getPosition()) {
                    allStation.add(index, branch.stations.get(order));
                    index++;
                }

            }
            order++;
        }
        return allStation;
    }

    /**
     * @param station add station to the station list
     */
    public void addStation(Station station) {
        int order = station.getOrder();
        stations.put(order, station);
    }

    /**
     * @param id number of the canton
     * @return Canton by id if it's not a terminus, else throw TerminusExeption
     * @see Canton
     */
    public Canton getCanton(int id, boolean way) {
        return cantons.get(id);
    }

    /**
     * get the start position of the branch
     *
     * @return the start position of the branch
     * @deprecated
     */
    public int calculateStartPosition() {
        int startPosition = 10000;
        for (Station tmp : Line.getInstance().getBranch(backBranchId).stations.values()) {
            if (startPosition > tmp.getPosition()) {
                startPosition = tmp.getPosition();
            }
        }
        return startPosition + 1;
    }

    public int getStartPosition() {
        return startPosition;
    }

    /**
     * @param name name of the station
     * @return Station by name
     * @see Station
     */
    public Station getStation(String name) {
        for (Station tmp : stations.values()) {
            if (tmp.getNameStation().equals(name)) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * @return list of Canton
     * @see Canton
     */
    public HashMap<Integer, Canton> getCantons() {
        return cantons;
    }

    /**
     * @return list of Station
     * @see Station
     */
    public HashMap<Integer, Station> getStations() {
        return stations;
    }

    /**
     * @return Branch name
     */
    public String getIdBranch() {
        return idBranch;
    }

    /**
     * @return total lenght
     */
    public int getTotalLength() {
        return totalLength;
    }

    /**
     * @param position is the position of the station
     */
    public Station getStationByPosition(int position) {
        for (Station tmp : stations.values()) {
            if (tmp.getPosition() == position) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * @param way is the way of the train
     */
    public Canton getDepartureCanton(boolean way) throws TerminusException {
        if (way) {
            if (!cantons.get(cantons.size() - 1).isTerminus(way)) {
                return cantons.get(cantons.size() - 1);
            }
            throw new TerminusException();
        } else {
            if (!cantons.get(0).isTerminus(way)) {
                return cantons.get(0);
            }
            throw new TerminusException();
        }
    }

    public String getUpBranchId() {
        return upBranchId;
    }

    public String getDownBranchId() {
        return downBranchId;
    }

    public String getBackBranchId() {
        return backBranchId;
    }

    public ArrayList<String> getNextBranch(boolean way) {
        ArrayList<String> nextBranch = new ArrayList<>();
        if (way) {
            nextBranch.add(getBackBranchId());
            return nextBranch;
        } else {
            nextBranch.add(getDownBranchId());
            nextBranch.add(getUpBranchId());
            return nextBranch;
        }
    }

    public int enter(boolean way) {
        return 0;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int delta_Y) {
        if (this.getBackBranchId() == null || this.getBackBranchId().equals("none")) {
            yPosition = 0;
        } else {
            Branch backBranch = Line.getInstance().getBranch(this.getBackBranchId());
            int compteurBackBranch = 0;
            Branch branchTmp = this;
            while (!branchTmp.getBackBranchId().equals("none") && (branchTmp.getBackBranchId() != null)) {
                branchTmp = Line.getInstance().getBranch(branchTmp.getBackBranchId());
                compteurBackBranch++;
                if (compteurBackBranch > 1) {
                    if (backBranch.getDownBranchId().equals(this.getIdBranch()))
                        yPosition = -delta_Y / ((compteurBackBranch - 1) * 2);
                    else if (backBranch.getUpBranchId().equals(this.getIdBranch()))
                        yPosition = -delta_Y / ((compteurBackBranch - 1) * 2);
                }
            }
            if (backBranch.getDownBranchId().equals(this.getIdBranch())) {
                //on est dans une branche du bas
                yPosition += (delta_Y) / (compteurBackBranch * 2);
            } else if (backBranch.getUpBranchId().equals(this.getIdBranch())) {
                //on est dans une branche du haut
                yPosition -= (delta_Y) / (compteurBackBranch * 2);
            }
        }
    }
}
