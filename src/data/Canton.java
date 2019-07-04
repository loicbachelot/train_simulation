package data;

import log.LoggerUtility;
import org.apache.log4j.Logger;

/**
 * Created by mady on 08/01/17.
 */
public class Canton {

    private final static Logger logger = LoggerUtility.getLogger(Canton.class);
    private int idCanton;
    private int startPoint;
    private int distance;
    private boolean trainGo;
    private boolean trainBack;
    private boolean terminusGo;
    private boolean terminusBack;

    /**
     * @param idCanton   number of the canton
     * @param startPoint canton start point
     * @param distance   canton size
     * @param terminus   is terminus
     */
    public Canton(int idCanton, int startPoint, int distance, boolean terminus) {
        this.idCanton = idCanton;
        this.startPoint = startPoint;
        this.distance = distance;
        this.trainGo = false;
        this.trainBack = false;
        if (idCanton == 0) {
            this.terminusBack = terminus;
            this.terminusGo = false;
        } else {
            this.terminusGo = terminus;
            this.terminusBack = false;
        }
    }

    /**
     * @param train train to move in
     */
    public synchronized int enter(Train train) {
        if ((!train.isWay() && trainGo) || (train.isWay() && trainBack)) {
            logger.trace(toString() + " is occupied");
            // Train stopped just before canton start point !
            if (!train.isWay()) {
                train.setPosition(startPoint - 1);
            } else {
                train.setPosition(getEndPoint() + 1);
            }
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());

            }
            return 1;
        }
        Canton oldCanton = (train.getCurrentCanton());
        train.setCurrentCanton(this);

        oldCanton.exit(train.isWay());
        if (train.isWay()) {
            trainBack = true;
        } else {
            trainGo = true;
        }
        return 0;
    }

    /**
     * @param way way of the train to free
     */
    public synchronized void exit(boolean way) {
        if (way) {
            trainBack = false;
        } else {
            trainGo = false;
        }
        notify();
    }

    /**
     * @return end point
     */
    public int getEndPoint() {
        return startPoint + distance;
    }

    /**
     * @return canton number
     */
    public int getID() {
        return idCanton;
    }

    /**
     * @return start point
     */
    public int getStartPoint() {
        return startPoint;
    }

    /**
     * @return if the train goes to the terminus
     */
    public boolean isTrainGo() {
        return trainGo;
    }

    /**
     * @return if the train get back from the terminus
     */
    public boolean isTrainBack() {
        return trainBack;
    }

    /**
     * @return is terminus
     */
    public boolean isTerminus(boolean way) {
        if (way) {
            return terminusBack;
        } else {
            return terminusGo;
        }
    }

    @Override
    /**
     * @return the if of the canton
     */
    public String toString() {
        return "Canton [id=" + idCanton + "]";
    }

    /**
     * @param way is the way of the train
     * @return if the train is on a way or another
     */
    public boolean isFree(boolean way) {
        if (way) {
            return !isTrainBack();
        } else {
            return !isTrainGo();
        }
    }

    /**
     * @param way set the train's way
     */
    public void setTrain(boolean way) {
        if (way) {
            trainBack = true;
        } else {
            trainGo = true;
        }
    }
}
