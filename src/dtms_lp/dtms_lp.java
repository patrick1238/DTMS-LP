/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtms_lp;

import Windowcontroller.SplashScreenController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author patri
 */
public class dtms_lp extends Application{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        start_splashscreen(primaryStage);
    }
    
    public void start_splashscreen(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/SplashScreen.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(dtms_lp.class.getName()).log(Level.SEVERE, null, ex);
        }
        final SplashScreenController controller = fxmlLoader.getController();
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();       
    }
    
}
