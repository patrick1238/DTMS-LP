/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Windowcontroller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author patri
 */
public class ErrorLogWindowController implements Initializable {

    @FXML
    private GridPane errorPane;
    
    int index = 0;
    private ArrayList<Label> current = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    public void addMessage(int type,String message){
        Label label = new Label(message);
        Label typeLabel = null;
        switch(type){
            case 0:
                typeLabel = new Label("Processed:");
                typeLabel.setTextFill(javafx.scene.paint.Color.GREEN);
                break;
            case 1:
                typeLabel = new Label("Warning:");
                typeLabel.setTextFill(javafx.scene.paint.Color.DARKORANGE);
                break;
            case 2:
                typeLabel = new Label("Error:");
                typeLabel.setTextFill(javafx.scene.paint.Color.RED);
                break;
        }
        int runner = 2;
        for(Label child :this.current){
            if((runner/2)<10){
                GridPane.setRowIndex((Node)child, (runner/2));
                runner += 1;
            }else{
                this.errorPane.getChildren().remove((Node)child);
                runner += 1;
            }
        }
        this.current.add(0,typeLabel);
        this.current.add(0,label);
        this.errorPane.addRow(0, typeLabel,label);
        index += 1;
    }    
}
