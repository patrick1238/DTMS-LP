/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Windowcontroller;

import Background_handler.CaseDatabase;
import Background_handler.Config;
import Background_handler.ErrorLog;
import Background_handler.ViewController;
import EventHandler.CloseRequest;
import EventHandler.DoubleClick_mouseevent;
import EventHandler.SearchFieldHandler;
import Interfaces.DTMSView;
import Objects.Case;
import Objects.Slice;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author patri
 */
public class Database_viewController implements Initializable, DTMSView {

    @FXML
    private MenuBar MenuBar;
    @FXML
    private MenuItem Add_case;
    @FXML
    private Circle statusCircle;
    @FXML
    private TableView<Case> Case_table;
    @FXML
    private TextField SearchField;

    CaseDatabase database;
    String viewID;
    ErrorLog log;
    SearchFieldHandler handler;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ErrorLog.initializeErrorLog(statusCircle);
        this.log = ErrorLog.getErrorLog();
        this.log.createLogEntry(0, "DTMS-LP started");
        this.database = CaseDatabase.getCaseDatabase();
        Case_table.getColumns().clear();
        TableColumn caseID = new TableColumn("CaseID");
        caseID.setCellValueFactory(new PropertyValueFactory<Slice, String>("CaseID"));
        Case_table.getColumns().add(caseID);
        TableColumn diagnosis = new TableColumn("Diagnosis");
        diagnosis.setCellValueFactory(new PropertyValueFactory<Slice, String>("Diagnosis"));
        Case_table.getColumns().add(diagnosis);
        TableColumn status = new TableColumn("Status");
        status.setCellValueFactory(new PropertyValueFactory<Slice, String>("Status"));
        Case_table.getColumns().add(status);
        Case_table.setOnMouseClicked(new DoubleClick_mouseevent(Case_table));
        Case_table.setItems(this.database.get_all_cases());
        TableColumn remove = new TableColumn("Remove");
        remove.setCellValueFactory(new PropertyValueFactory<Slice, Button>("Remove"));
        Case_table.getColumns().add(remove);
        this.viewID = "MainView";
        this.handler = new SearchFieldHandler(SearchField,Case_table,this.database.get_all_cases());
    }

    @FXML
    private void add_case_clicked(ActionEvent event) {
        Stage stage = new Stage();
        Parent root = null;
        Config config = Config.getConfig();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Add_Case.fxml"));
        try {
            root = fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(SplashScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        final Add_CaseController controller = fxmlLoader.getController();
        ViewController viewcontroller = ViewController.getViewController();
        boolean not_existing = viewcontroller.addView(controller);
        if (not_existing) {
            stage.setOnCloseRequest(new CloseRequest(controller.getViewID()));
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/Styles.css");
            stage.setTitle("Add case");
            stage.setScene(scene);
            stage.show();
        }
    }


    @FXML
    private void closeStatus(MouseEvent event) {
        this.log.close();
    }

    @FXML
    private void displayStatus(MouseEvent event) {
        Point2D point = this.statusCircle.localToScreen(0,0);
        this.log.display(point.getX(),point.getY());
    }

    @Override
    public String getViewID() {
        return this.viewID;
    }

    @Override
    public void updateView() {
        Case_table.setItems(this.database.get_all_cases());
        Case_table.refresh();
        this.handler.set_cases(this.database.get_all_cases());
    }

    @Override
    public void close() {
        ViewController controller = ViewController.getViewController();
        controller.closeChildViews();
        this.database.close();
        Stage stage = (Stage) Case_table.getScene().getWindow();
        stage.close();
    }
}
