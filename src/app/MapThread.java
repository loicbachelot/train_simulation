package app;

import data.*;
import data.Timer;
import factory.XMLReader;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mady on 08/01/17.
 *
 * @author mady
 */
public class MapThread extends Thread {
    public static final int TIME_UNIT_TRAIN = 50;
    public static final int TIME_UNIT = 50;
    private final static Logger logger = LoggerUtility.getLogger(MapThread.class);
    private static final int START_X = 350;
    private static final int START_Y = 500;
    private static final int DELTA_Y = 250;
    private static final int TRAIN_SPEED_VARIATION = 0;
    private static final int TRAIN_BASIC_SPEED = 10;
    private static final Date SIMULATION_End = new Date(new Date().getTime() + 200000);
    private data.Line line;
    private List<Train> trains = new ArrayList<Train>();
    private Collection<Station> stations = new ArrayList<>();
    private volatile Timer timer;
    private Pane mapPane;
    private Label clock;
    private Label stationNameLabel;
    private HashMap<Integer, Integer> departureTime = new HashMap<>();
    private volatile Boolean paused = false;
    private Label infoLabel;
    private int numberOfTrainStopped;
    private GridPane timeTablePane;
    private Image RAIL = new Image(("file:BOnTime_test/src/app/res/icon/rail2.png"));
    private Image GARE = new Image(("file:BOnTime_test/src/app/res/icon/Gare.png"));
    private Image TRAINGO = new Image(("file:BOnTime_test/src/app/res/icon/TrainGo.png"));
    private Image TRAINBACK = new Image(("file:BOnTime_test/src/app/res/icon/TrainBack.png"));

    /**
     * @param mapPane is the map pane
     */
    public MapThread(Pane mapPane, Label clock, Label stationNameLabel, Label infoLabel, GridPane timeTablePane) {
        super();
        this.mapPane = mapPane;
        XMLReader xmlReader = new XMLReader("BOnTime_test/linev11.xml");
        xmlReader.buildData();
        line = xmlReader.getLine();
        timer = Timer.getInstance();
        this.clock = clock;
        this.stationNameLabel = stationNameLabel;
        this.timeTablePane = timeTablePane;
        this.infoLabel = infoLabel;
        infoLabel.setTextFill(Color.WHITE);
        infoLabel.setText("The traffic is normal");
        for (Branch tmpBranch : Line.getInstance().getBranches().values()) {
            tmpBranch.setyPosition(DELTA_Y);
        }
        repaint();
    }

    /**
     * @param train is the train added
     */
    public synchronized void addTrain(Train train) {
        trains.add(train);
    }

    /**
     * @param station is the station added
     */
    public synchronized void addStation(Station station) {
        stations.add(station);
    }


    private synchronized void printLine(data.Line line) {
        printBranches(line, line.getBranch("Paris Saint-Lazare;Bécon les Bruyères"), 0, 0, DELTA_Y, START_Y);
        printAllStationsRecursive(line, line.getBranch("Paris Saint-Lazare;Bécon les Bruyères"), 0, 0, DELTA_Y);
        //printAllStationsIteratif();
    }

    /**
     * print all branches
     */
    private synchronized void printBranches(data.Line line, Branch branch, int Y, int X, int delta_y, int startY) {
        stations = branch.getStations().values();
        int endPoint = branch.getTotalLength();
        String keys[] = branch.getIdBranch().split(";");
        int startPoint;
        if (line.getBranch(branch.getBackBranchId()) != null) {
            startPoint = line.getBranch(branch.getBackBranchId()).getTotalLength();
            if (line.getBranch(branch.getBackBranchId()).getUpBranchId().equals(branch.getIdBranch())) {
                Branch backBranch = line.getBranch(branch.getBackBranchId());
//                int compteurBackBranch = 0;
//                Branch branchTmp = branch;
//                while (!branchTmp.getBackBranchId().equals("none") && (branchTmp.getBackBranchId() != null)) {
//                    branchTmp = line.getBranch(branchTmp.getBackBranchId());
//                    compteurBackBranch++;
//                }
                javafx.scene.shape.Line separator = new javafx.scene.shape.Line();
                separator.setStrokeWidth(5);
                separator.setFill(Color.BLACK);
                separator.setStartX(START_X + startPoint);
                separator.setStartY(START_Y - Y - 5);
                separator.setEndX(START_X + startPoint);
                separator.setEndY(startY + Y + 28);
                mapPane.getChildren().add(separator);
                startY = startY - (Y + delta_y);
            }
        } else {
            startPoint = branch.getStation(keys[0]).getPosition();
        }
        for (int i = startPoint; i < endPoint; i += 50) {
            ImageView iv1 = new ImageView(RAIL);
            iv1.relocate(START_X + i, START_Y - (Y - 10));
            mapPane.getChildren().add(iv1);
            ImageView iv2 = new ImageView(RAIL);
            iv2.relocate(START_X + i, START_Y - (Y + 13));
            mapPane.getChildren().add(iv2);
        }
//        for (int i = startPoint; i < endPoint; i += 50) {
//            ImageView iv = new ImageView(RAIL);
//            iv.relocate(START_X + i, START_Y - (Y + 13));
//            mapPane.getChildren().add(iv);
//        }
        X += branch.getTotalLength();
        delta_y = delta_y / 2;
        if (!branch.getDownBranchId().equals("none")) {
            printBranches(line, line.getBranch(branch.getDownBranchId()), Y - delta_y, X, delta_y, startY);
        }
        if (!branch.getUpBranchId().equals("none")) {
            printBranches(line, line.getBranch(branch.getUpBranchId()), Y + delta_y, X, delta_y, startY);
        }
    }

    /**
     * print all stations
     */
    private synchronized void printAllStationsRecursive(data.Line line, Branch branch, int Y, int X, int delta_y) {
        stations = branch.getStations().values();
        printStations(Y);
        X += branch.getTotalLength();
        delta_y = delta_y / 2;
        if (!branch.getDownBranchId().equals("none")) {
            printAllStationsRecursive(line, line.getBranch(branch.getDownBranchId()), Y - delta_y, X, delta_y);
        }
        if (!branch.getUpBranchId().equals("none")) {
            printAllStationsRecursive(line, line.getBranch(branch.getUpBranchId()), Y + delta_y, X, delta_y);
        }
    }

    private void printAllStations() {
        int x = 0;
        for (Branch tmpBranch : Line.getInstance().getBranches().values()) {
            stations = tmpBranch.getStations().values();
            printStations(tmpBranch.getyPosition());
            x += tmpBranch.getTotalLength();
        }
    }


    /**
     * print all trains
     */
    private synchronized void printTrains() {
        Branch branch;
        ImageView iv;
        for (Train train : trains) {
            branch = train.getBranch();
            int y = branch.getyPosition();
//            if (branch.getBackBranchId() == null || branch.getBackBranchId().equals("none")) {
//                y = 0;
//            } else {
//                Branch backBranch = line.getBranch(branch.getBackBranchId());
//                int compteurBackBranch = 0;
//                Branch branchTmp = branch;
//                while (!branchTmp.getBackBranchId().equals("none") && (branchTmp.getBackBranchId() != null)) {
//                    branchTmp = line.getBranch(branchTmp.getBackBranchId());
//                    compteurBackBranch++;
//                    if (compteurBackBranch > 1) {
//                        if (backBranch.getDownBranchId().equals(branch.getIdBranch()))
//                            y = -DELTA_Y / ((compteurBackBranch - 1) * 2);
//                        else if (backBranch.getUpBranchId().equals(branch.getIdBranch()))
//                            y = -DELTA_Y / ((compteurBackBranch - 1) * 2);
//                    }
//                }
//                if (backBranch.getDownBranchId().equals(branch.getIdBranch())) {
//                    //on est dans une branche du bas
//                    y += (DELTA_Y) / (compteurBackBranch * 2);
//                } else if (backBranch.getUpBranchId().equals(branch.getIdBranch())) {
//                    //on est dans une branche du haut
//                    y -= (DELTA_Y) / (compteurBackBranch * 2);
//                }
//            }
            if (train.isWay()) {
                iv = new ImageView(TRAINGO);
                iv.relocate(START_X + train.getPosition(), START_Y - 8 + y);
                mapPane.getChildren().add(iv);
                //trainPointRound.setCenterY(START_Y);
            } else {
                iv = new ImageView(TRAINBACK);
                iv.relocate(START_X + train.getPosition(), START_Y + 18 + y);
                mapPane.getChildren().add(iv);
                //trainPointRound.setCenterY(START_Y + 25);
            }
            /*trainPointRound.setRadius(5.0);
            trainPointRound.setFill(Color.RED);
            mapPane.getChildren().add(trainPointRound);*/

            iv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    if (!train.isTrainAccident()) {
                        train.setTrainAccident(true);
                        logger.trace("Train " + train.getName() + " paused.");
                        numberOfTrainStopped++;
                        //itrainPointRound.setFill(Color.ORANGE);
                    } else {
                        train.setTrainAccident(false);
                        //trainPointRound.setFill(Color.RED);
                        numberOfTrainStopped--;
                    }
                    if (numberOfTrainStopped == 0) {
                        infoLabel.setTextFill(Color.WHITE);
                        infoLabel.setText("The traffic is normal");
                        logger.trace("Train " + train.getName() + " is OK now");
                    } else {
                        infoLabel.setTextFill(Color.RED);
                        infoLabel.setText("A train had an accident !");
                        logger.trace("Train " + train.getName() + " had an accident !");
                    }
                }
            });
            iv.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    mapPane.setCursor(javafx.scene.Cursor.HAND);

                }
            });
            iv.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    mapPane.setCursor(javafx.scene.Cursor.DEFAULT);

                }
            });
        }
    }


    /**
     * print all stations
     */
    private synchronized void printStations(int y) {
        for (Station station : stations) {
            ImageView iv = new ImageView(GARE);
            iv.relocate(START_X + station.getPosition() - 7, START_Y - 24 - y);
            mapPane.getChildren().add(iv);
            Rectangle stationToPrint = new Rectangle();
            stationToPrint.setX(START_X + station.getPosition());
            stationToPrint.setY(START_Y - 3 - y);
            stationToPrint.setWidth(5);
            stationToPrint.setHeight(35);
            stationToPrint.setFill(Color.BLACK);
            mapPane.getChildren().add(stationToPrint);
            stationToPrint.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    // time
                    for (int i = 1; i <= 7; i++) {
                        Label test = (Label) timeTablePane.getChildren().get(i);
                        test.setText("");
                    }
                    for (int i = 8; i <= 11; i++) {
                        Label test = (Label) timeTablePane.getChildren().get(i);
                        test.setText("");
                    }

                    stationNameLabel.setText(station.getNameStation());
                    int index = 2;
                    Label labelEnCours;
                    for (Train train : trains) {
                        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
                        if (station.getTimeTable().containsKey(train)) {
                            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                            int sec = (int) (station.getTimeTable().get(train) % 60);
                            if ((station.getTimeTable().get(train) == 59))
                                sec = 59;
                            int min = (int) (station.getTimeTable().get(train) / 60);
                            int hours = 0;
                            if (min >= 60) {
                                hours = (int) (station.getTimeTable().get(train) / 60);
                                min %= 60;
                            }
                            String text = String.format("%02d", hours) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
                            labelEnCours = (Label) timeTablePane.getChildren().get(index);
                            labelEnCours.setText(text);
                            index++;
                        }
                    }
                    for (int i = index; i <= 7; i++) {
                        labelEnCours = (Label) timeTablePane.getChildren().get(index);
                        labelEnCours.setText("");
                    }
                    index = 7;
                    for (Train train : trains) {
                        if (station.getTimeTable().containsKey(train)) {
                            labelEnCours = (Label) timeTablePane.getChildren().get(index);
                            labelEnCours.setText(train.getDestination());
                            index++;
                        }
                    }
                    for (int i = index; i <= 12; i++) {
                        labelEnCours = (Label) timeTablePane.getChildren().get(index);
                        labelEnCours.setText("");
                    }

                }
            });
            stationToPrint.setOnMouseEntered(new EventHandler<MouseEvent>()

            {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    mapPane.setCursor(javafx.scene.Cursor.HAND);

                }
            });
            stationToPrint.setOnMouseExited(new EventHandler<MouseEvent>()

            {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    mapPane.setCursor(javafx.scene.Cursor.DEFAULT);

                }
            });
            iv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    // time
                    for (int i = 1; i <= 7; i++) {
                        Label test = (Label) timeTablePane.getChildren().get(i);
                        test.setText("");
                    }
                    for (int i = 8; i <= 11; i++) {
                        Label test = (Label) timeTablePane.getChildren().get(i);
                        test.setText("");
                    }
                    stationNameLabel.setText(station.getNameStation());
                    int index = 2;
                    Label labelEnCours;
                    for (Train train : trains) {
                        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
                        if (station.getTimeTable().containsKey(train)) {
                            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                            int sec = (int) (station.getTimeTable().get(train) % 60);
                            if ((station.getTimeTable().get(train) == 59))
                                sec = 59;
                            int min = (int) (station.getTimeTable().get(train) / 60);
                            int hours = 0;
                            if (min >= 60) {
                                hours = (int) (station.getTimeTable().get(train) / 60);
                                min %= 60;
                            }
                            String text = String.format("%02d", hours) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
                            labelEnCours = (Label) timeTablePane.getChildren().get(index);
                            labelEnCours.setText(text);
                            index++;
                        }
                    }
                    for (int i = index; i <= 7; i++) {
                        labelEnCours = (Label) timeTablePane.getChildren().get(index);
                        labelEnCours.setText("");
                    }
                    index = 7;
                    for (Train train : trains) {
                        if (station.getTimeTable().containsKey(train)) {
                            labelEnCours = (Label) timeTablePane.getChildren().get(index);
                            labelEnCours.setText(train.getDestination());
                            index++;
                        }
                    }
                    for (int i = index; i <= 12; i++) {
                        labelEnCours = (Label) timeTablePane.getChildren().get(index);
                        labelEnCours.setText("");
                    }

                }
            });
            iv.setOnMouseEntered(new EventHandler<MouseEvent>()

            {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    mapPane.setCursor(javafx.scene.Cursor.HAND);

                }
            });
            iv.setOnMouseExited(new EventHandler<MouseEvent>()

            {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    mapPane.setCursor(javafx.scene.Cursor.DEFAULT);

                }
            });
        }
    }

    /**
     * @return the line
     */

    public synchronized data.Line getLine() {
        return line;
    }

    /**
     * @param line is the new line
     */
    public synchronized void setLine(data.Line line) {
        this.line = line;
    }

    /**
     * @return all trains
     */
    public synchronized List<Train> getTrains() {
        return trains;
    }

    /**
     * @param trains is the list of the new trains
     */
    public synchronized void setTrains(List<Train> trains) {
        this.trains = trains;
    }

    /**
     * @return is if button is paused
     */
    public Boolean isPaused() {
        return paused;
    }

    /**
     * Run while it is not paused
     */
    @Override
    public void run() {
        int numberDeparture;
        synchronized (Timer.class) {
            timer.restart();
            for (Branch branch : line.getBranches().values()) {
                for (Station station : branch.getStations().values()) {
                    addStation(station);
                }
            }
            while (timer.getLast().before(SIMULATION_End)) {
                try {
                    Thread.sleep(TIME_UNIT);
                } catch (InterruptedException e) {
                    logger.error("Error sleep : ", e);
                }
                try {
                    repaint();
                    for (Branch branch : line.getBranches().values()) {

                        numberDeparture = line.numberDeparturesTrainByBranch(branch.getIdBranch(), timer.getLast());

                        for (int i = 0; i < numberDeparture; i++) {
                            data.Line line = getLine();
                            Train train = line.getTrainToGO(branch.getIdBranch(), timer.getLast());
                            train.initTrain();
                            logger.trace("Current canton : " + train.getCurrentCanton());
                            if (!train.getCurrentCanton().isFree(train.isWay())) {
                                addTrain(train);
                                train.start();
                                logger.trace("Train " + train.toString() + " created");
                            }
                        }

                    }
                    for (Train train :
                            trains) {
                        if (train.isRetire()) {
                            trains.remove(train);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("Error while running : ", e);
                }
            }

        }
    }

    /**
     * repaint the pane
     */
    private synchronized void repaint() {
        if (!paused) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mapPane.getChildren().clear();
                    printLine(line);
                    printTrains();
                    printTime();
                }
            });
        }

    }

    /**
     * Print the time
     */
    private synchronized void printTime() {
        clock.setText(timer.toString());
    }

    /**
     * Init all departures
     */
    private synchronized void initDeparture() {
        for (int i = 0; i < 100; i++) {
            departureTime.put(i * 7, i);
        }
    }

    /**
     * return the time of simulation
     *
     * @return Timer timer
     * @see Timer
     */
    public synchronized Timer getTimer() {
        return timer;
    }


    /**
     * set the simulation timer to a value
     *
     * @param timer time of the simulation
     * @see Timer
     */
    public synchronized void setTimer(Timer timer) {
        this.timer = timer;
    }


    /**
     * Put the simulation in paused mode
     */
    public void pauseSimulation() {
        timer.setSpeed(0);
        paused = true;
        logger.info("Simulation paused");
    }

    /**
     * Make the simulation run again
     */
    public void playSimulation() {
        timer.setSpeed(1);
        paused = false;
        logger.info("Simulation runs");
    }

    public void setSpeed(DoubleProperty speed) {
        timer.setSpeed(speed);

        logger.info("speed changed to" + speed);
    }

    public synchronized void heal() {
        for (Train train : getTrains()) {
            if (train.isTrainAccident()) {
                train.setTrainAccident(false);
                logger.info("train repaired" + train);
            }

        }
    }

    public synchronized void purge() {
        for (Train train : getTrains()) {
            if (train.isTrainAccident()) {
                train.retire();

                logger.info("train deleted" + train);
            }

        }
    }
}
