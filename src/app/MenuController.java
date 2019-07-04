package app;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MenuController {

    @FXML
    public JFXButton launchButton;

    @FXML
    public Pane imgPane;

    private MainApp mainApp;

    public MenuController() {
    }

    /**
     * The the main app
     */
    public void mapAction() throws IOException {
        this.mainApp.showMapView();
    }

    /**
     * @param mainApp is setting up
     */
    public void setMain(MainApp mainApp) {
        this.mainApp = mainApp;
        mainApp.getRootController().getPreviousHamburger().setVisible(false);
        Image image = new Image(("file:BOnTime_test/src/app/res/icon/giphy.gif"));
        ImageView imageView = new ImageView(image);
        imgPane.getChildren().add(imageView);
    }

}
