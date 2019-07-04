package app;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Created by mady on 08/01/17.
 *
 * @author mady
 */
public class MapController {
    public ImageView playButton;
    public ImageView fastButton;
    DoubleProperty previousSpeed;
    private MainApp mainApp;
    private MapThread mapThread;
    @FXML
    private Label clock;
    @FXML
    private Pane mapPane;
    @FXML
    private Label stationNameLabel;
    @FXML
    private GridPane timeTablePane;
    @FXML
    private Slider speedSlider;

    /**
     * empty constructor
     */
    public MapController() {

    }

    @FXML
    private void initialize() {

    }

    /**
     * Set the the Map as main screen of the app
     *
     * @param mainApp app where to set the map as main
     * @see MainApp
     */
    public synchronized void setMain(MainApp mainApp) {

        this.mainApp = mainApp;
        mainApp.getRootController().getPreviousHamburger().setVisible(true);
        mainApp.getRootController().getPreviousHamburger().addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            mapThread.interrupt();
            mainApp.showMenuOverview();
            mainApp.getRootController().getInfoLabel().setTextFill(Color.WHITE);
            mainApp.getRootController().getInfoLabel().setText("BonTime is ready");
        });
        mapThread = new MapThread(mapPane, clock, stationNameLabel, mainApp.getRootController().getInfoLabel(), timeTablePane);
        speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                speedEvent();
            }
        });
        mapThread.start();
    }

    /**
     * Change to play or pause
     */
    public synchronized void playPauseButton() throws InterruptedException {
        if (!mapThread.isPaused()) {
            playButton.setImage(new Image(("file:BOnTime_test/src/app/res/icon/play.png")));

            previousSpeed = speedSlider.valueProperty();

            System.out.println("previousSpeed = " + previousSpeed.doubleValue());
            speedSlider.setDisable(true);
            mapThread.pauseSimulation();
        } else {
            playButton.setImage(new Image(("file:BOnTime_test/src/app/res/icon/pause.png")));
            speedSlider.setDisable(false);
            mapThread.playSimulation();

            mapThread.setSpeed(previousSpeed);
        }
    }

    public synchronized void speedEvent() {

        mapThread.setSpeed(speedSlider.valueProperty());
    }


    public synchronized void healAction() {

        mapThread.heal();
        mainApp.getRootController().getInfoLabel().setTextFill(Color.GREEN);
        mainApp.getRootController().getInfoLabel().setText("all trains are being repaired");
    }

    public synchronized void purgeAction() {

        mapThread.purge();

        mainApp.getRootController().getInfoLabel().setTextFill(Color.ORANGE);
        mainApp.getRootController().getInfoLabel().setText("all  broken trains are being suppressed");

    }


}
