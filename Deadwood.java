import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import controller.GameController;
import controller.MenuController;
import model.GameManager;

/* Deadwood.java

   Runs an instance of the Game Deadwood. Compete to earn the most money and credits as a Western Bit Actor.

   Requires JavaFX -- Developed and Compiled with JavaFX 11 and JFoenix for GUI and Design Elements

   For instructions and more, see the original deadwood board game that inspired this program:
       Original Deadwood Board Game: "cheapass.com/free-games/deadwood/ from Seattle, WA */


public class Deadwood extends Application {

    private static GameManager gm;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("DeadWood");
        primaryStage.setMinWidth(450);
        primaryStage.setMinHeight(300);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());

        //Generate FXML Loaders for each fxml file. One for main menu and one for the game board
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("view/main_menu.fxml"));
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("view/board.fxml"));

        //Generate Roots for each loader
        Parent menuRoot = menuLoader.load();
        Parent gameRoot = gameLoader.load();

        //Build Scene and pass main menu as the original root
        Scene scene = new Scene(menuRoot);

        //Build the Controllers that handle input for main menu and gameboard
        MenuController menuController = menuLoader.getController();
        GameController gameController = gameLoader.getController();

        //Pass each controller a reference to each other and the main scene
        menuController.setScene(scene);
        gameController.setScene(scene);
        menuController.setRootGame(gameRoot);
        gameController.setRootMenu(menuRoot);

        GameManager.getInstance().setMenuRoot(menuRoot);

        //Set Scene and Show Stage
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}