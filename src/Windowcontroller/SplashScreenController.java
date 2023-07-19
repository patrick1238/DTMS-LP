/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Windowcontroller;

import Background_handler.Config;
import Background_handler.PreLoader;
import Background_handler.ViewController;
import EventHandler.CloseRequest;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author patri
 */
public class SplashScreenController implements Initializable {

    @FXML
    ImageView imageview;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new ShowSplashScreen().start();
    }

    class ShowSplashScreen extends Thread {
        @Override
        public void run() {
            PreLoader.load_backgroundinformation();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(SplashScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
            Platform.runLater(() -> {
                Stage stage = new Stage();
                Parent root = null;
                Config config = Config.getConfig();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Database_view.fxml"));
                try {
                    root = fxmlLoader.load();
                } catch (IOException ex) {
                    Logger.getLogger(SplashScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }
                final Database_viewController controller = fxmlLoader.getController();
                ViewController viewController = ViewController.getViewController();
                viewController.addView(controller);
                stage.setOnCloseRequest(new CloseRequest(controller.getViewID()));
                Scene scene = new Scene(root);
                scene.getStylesheets().add("/styles/Styles.css");
                stage.setTitle("DTMS-LP");
                stage.setScene(scene);
                stage.show();
                imageview.getScene().getWindow().hide();
            });
        }

    }

}
