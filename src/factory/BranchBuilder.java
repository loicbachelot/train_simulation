package factory;

import data.Branch;
import data.Station;

import java.util.HashMap;

/**
 * Created by mady on 25/01/17.
 *
 * @author mady
 */
public class BranchBuilder {


    private Branch branch;

    /**
     * @param totalLength is le length of the new branch
     * @param cantonSize  is the new size of cantons
     * @param ID          is the ID of the new branch
     * @return the new branch
     */
    public Branch buildBranch(String ID, int totalLength, int cantonSize, HashMap<String, Station> stations) {
        return branch = new Branch(ID, totalLength, cantonSize);

    }

    /**
     * @return the branch
     */
    public Branch getBuildBranch() {
        return branch;
    }

}