/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Background_handler;

import Windowcontroller.ErrorLogWindowController;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author patri
 */
public class ErrorLog {

    private static ErrorLog log;
    private static PrintStream out;

    Stage stage;
    ErrorLogWindowController controller;
    Circle statusCircle;
    ObservableList<Entry<Integer, String>> statusMessages;

    private ErrorLog(Circle statusCircle) {
        this.statusMessages = FXCollections.observableArrayList();
        this.statusCircle = statusCircle;
        stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/fxml/ErrorLogWindow.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException ex) {

        }
        controller = fxmlLoader.getController();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setScene(scene);
    }

    public static void initializeErrorLog(Circle statusCircle) {
        log = new ErrorLog(statusCircle);
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        File error = new File(Config.getConfig().get("errorlog_path")+ft.format(dNow)+".txt");
        if (!error.exists()) {
            try {
                error.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ErrorLog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            out = new PrintStream(new FileOutputStream(error.getAbsolutePath()));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ErrorLog.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setErr(out);

    }

    public static ErrorLog getErrorLog() {
        if (log == null) {
            throw new NullPointerException("ErrorLog: not initialized");
        } else {
            return log;
        }
    }

    public void createLogEntry(int type, String message) {
        switch (type) {
            case 0:
                this.statusMessages.add(new SimpleEntry(type, message));
                controller.addMessage(0, message);
                this.statusCircle.setFill(javafx.scene.paint.Color.GREEN);
                Logger.getLogger(ErrorLog.class.getName()).log(Level.INFO, message);
                break;
            case 1:
                this.statusMessages.add(new SimpleEntry(type, message));
                controller.addMessage(1, message);
                this.statusCircle.setFill(javafx.scene.paint.Color.YELLOW);
                Logger.getLogger(ErrorLog.class.getName()).log(Level.WARNING, message);
                break;
            case 2:
                this.statusMessages.add(new SimpleEntry(type, message));
                controller.addMessage(2, message);
                this.statusCircle.setFill(javafx.scene.paint.Color.RED);
                Logger.getLogger(ErrorLog.class.getName()).log(Level.SEVERE, message);
                break;
        }
    }

    public void display(double xPosition, double yPosition) {
        if (!stage.isShowing()) {
            stage.show();
            stage.setX(xPosition - stage.getWidth());
            stage.setY(yPosition);
        }
    }

    public void close() {
        this.stage.close();
    }

}
