package data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by mady on 08/01/17.
 *
 * @author mady
 *         Singleton
 */
public class Line {
    private static Line INSTANCE = new Line();
    private char ID;
    private HashMap<String, Branch> branches;
    private int totalLength;
    private HashMap<String, ArrayList<Train>> trainList;

    private Line() {
        branches = new HashMap<>();
        trainList = new HashMap<>();
    }

    public static Line getInstance() {
        return INSTANCE;
    }

//    /**
//     * @param ID char that defines the line
//     */
//    public Line(char ID) {
//        this.ID = ID;
//        branches = new HashMap<>();
//        trainList = new HashMap<>();
//    }


    /**
     * @return the line's ID
     */
    public char getId() {
        return ID;
    }


    /**
     * @return the HasMap of branches
     */
    public HashMap<String, Branch> getBranches() {
        return branches;
    }


    /**
     * @param key used to select the specific branch
     * @return the selected branch
     */
    public Branch getBranch(String key) {
        return branches.get(key);
    }


    /**
     * @param branch adding to the line
     */
    public void addBranch(Branch branch) {
        branches.put(branch.getIdBranch(), branch);
        trainList.put(branch.getIdBranch(), new ArrayList<>());
    }


    /**
     * @param branchID selectiong the specific branch
     * @param train    adding to the line
     */
    public void addTrain(String branchID, Train train) {
        trainList.get(branchID).add(train);
    }


    /**
     * @param branchID selectiong the specific branch
     * @param station  adding to the line
     */
    public void addStation(String branchID, Station station) {
        getBranch(branchID).addStation(station);
    }

    /**
     * @param IDBranch    selectiong the specific branch
     * @param currentTime is the current time
     * @return the number of train departures for a branch
     */
    public int numberDeparturesTrainByBranch(String IDBranch, Date currentTime) {
        int number = 0;
        for (Train train : trainList.get(IDBranch)) {
            if (train.getDepartureTime().before(currentTime) && !train.isOnLine()) {
                number++;
            }

        }
        return number;
    }

    /**
     * @param IDBranch    id of the specific branch
     * @param currentTime is the current time
     * @return return the train
     */
    public Train getTrainToGO(String IDBranch, Date currentTime) {
        for (Train train : trainList.get(IDBranch)) {
            if (train.getDepartureTime().before(currentTime) && !train.isOnLine()) {
                return train;
            }
        }
        return null;
    }

    /**
     * @return the total length of a line
     */
    public int getTotalLength() {
        return totalLength;
    }

    public ArrayList<Branch> calculateAllBranches(String trainType) {
        ArrayList<Branch> branches = new ArrayList<>();
        for (Branch tmpBranch : this.getBranches().values()) {
            if (tmpBranch.getTrainsTypeList().contains(trainType)) {
                branches.add(tmpBranch);
            }
        }
        return branches;
    }
}
