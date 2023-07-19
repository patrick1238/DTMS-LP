/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EventHandler;

import Background_handler.Config;
import Background_handler.ViewController;
import Objects.Case;
import Windowcontroller.Case_viewController;
import Windowcontroller.SplashScreenController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author patri
 */
public class DoubleClick_mouseevent implements EventHandler<MouseEvent> {

    private TableView<Case> Case_table;

    public DoubleClick_mouseevent(TableView<Case> Case_table) {
        this.Case_table = Case_table;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            Case cur = Case_table.getSelectionModel().getSelectedItem();
            boolean existing = cur.observe_case_csv();
            if(!existing){
                cur.setStatus("generated");
            }
            Stage stage = new Stage();
            Parent root = null;
            FXMLLoader fxmlLoader = new FXMLLoader(DoubleClick_mouseevent.this.getClass().getResource("/fxml/Case_view.fxml"));
            try {
                root = fxmlLoader.load();
            } catch (IOException ex) {
                Logger.getLogger(SplashScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
            final Case_viewController controller = fxmlLoader.getController();
            controller.view_case(cur);
            ViewController viewcontroller = ViewController.getViewController();
            boolean not_existing = viewcontroller.addView(controller);
            if (not_existing) {
                stage.setOnCloseRequest(new CloseRequest(controller.getViewID()));
                Scene scene = new Scene(root);
                scene.getStylesheets().add("/styles/Styles.css");
                stage.setTitle("Print: " + cur.getCaseID());
                stage.setScene(scene);
                stage.show();
            }
        }
    }
}
