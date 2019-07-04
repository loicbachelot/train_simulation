package app;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import log.LoggerUtility;
import org.apache.log4j.Logger;

public class RootController {

    private final static Logger logger = LoggerUtility.getLogger(RootController.class);

    @FXML
    private JFXHamburger previousHamburger;
    @FXML
    private JFXHamburger closeHamburger;
    @FXML
    private Label infoLabel;

    private MainApp mainApp;

    public RootController() {
    }

    @FXML
    /**
     * init the root controller
     */
    private void initialize() {
        HamburgerBackArrowBasicTransition previousBurgerTask = new HamburgerBackArrowBasicTransition(previousHamburger);

        HamburgerBasicCloseTransition closeBurgerTask = new HamburgerBasicCloseTransition(closeHamburger);
        previousBurgerTask.setRate(1);
        closeBurgerTask.setRate(1);

        previousBurgerTask.play();
        closeBurgerTask.play();
    }

    @FXML
    /**
     * implementation of the close button
     */
    public void closeButtonAction() {
        logger.info("Simulation closed.");
        // get a handle to the stage
        Stage stage = (Stage) closeHamburger.getScene().getWindow();
        // do what you have to do
        stage.close();
        System.exit(0);

    }

    /**
     * @param mainApp init the mainApp
     */
    public void setMain(MainApp mainApp) {
        logger.info("App init OK !");
        this.mainApp = mainApp;
    }

    /**
     * @return the info label
     */
    public Label getInfoLabel() {
        return infoLabel;
    }


    /**
     * @return the previous Hamburger
     */
    public JFXHamburger getPreviousHamburger() {
        return previousHamburger;
    }



}
