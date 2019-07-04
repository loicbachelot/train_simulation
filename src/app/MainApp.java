package app;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.Callable;

public class MainApp extends Application {

    private final static Logger logger = LoggerUtility.getLogger(MainApp.class);
    private Stage primaryStage;
    private BorderPane rootLayout;
    private RootController rootController;
    private Callable currentScreen;
    private Callable previousScreen;

    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Starting method
     *
     * @param primaryStage stage were to start the application
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("B'OnTime");

        initRootLayout();
        showMenuOverview();
        primaryStage.setMaximized(true);

    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {

            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("res/fxml/RootLayout.fxml"));
            rootLayout = loader.load();

            setRootController(loader.getController());
            getRootController().setMain(this);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            logger.info("Scene init OK");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("initRootLayout error : ", e);
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showMenuOverview() {
        try {
            // Load person overview.
            menuInit();

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("showMenuOverview error", e);
        }
    }

    /**
     * Display the menu inside the root layout
     *
     * @throws IOException if the .jxml is not opened
     */
    public void menuInit() throws IOException {

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("res/fxml/Menu.fxml"));

        AnchorPane Overview = loader.load();

        // Set person overview into the center of root layout.
        rootLayout.setCenter(Overview);

        // Give the controller access to the main app.
        MenuController controller = loader.getController();
        controller.setMain(this); //TODO hide back button on the menu
        //TODO when ready, spinner hide
        logger.info("Menu init OK");
    }


    /**
     * @return Stage primaryStage of the app
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }


    /**
     * @return RootController root controller of the app
     * @see RootController
     */
    public RootController getRootController() {
        return rootController;
    }

    /**
     * setter of the rootController
     *
     * @param rootController RootController to set
     * @see RootController
     */
    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }

    /**
     * set the previous screen to the main screen
     */
    public void showPreviousScreen() {


    }

    /**
     * set the Map view to main screen
     *
     * @see MapThread
     */
    public void showMapView() throws IOException {
        primaryStage.setWidth(1100);
        primaryStage.setHeight(700);

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("res/fxml/Map.fxml"));
        AnchorPane Overview = loader.load();

        // Set person overview into the center of root layout.
        rootLayout.setCenter(Overview);

        // Give the controller access to the main app.
        MapController controller = loader.getController();
        controller.setMain(this);

    }


    /*public void setCurrentScreen(Callable func) {
        this.currentScreen = func;
    }

    public void setPreviousScreen(Callable func) {
        this.previousScreen = func;
    }*/

}
