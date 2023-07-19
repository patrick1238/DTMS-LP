/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EventHandler;

import Objects.Case;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author patri
 */
public class SearchFieldHandler implements EventHandler<KeyEvent> {
    
    private TextField field;
    TableView<Case> view;
    ObservableList<Case> cases;

    public SearchFieldHandler(final TextField field, final TableView view, ObservableList<Case> cases) {
        this.field = field;
        this.view = view;
        this.cases = cases;
        field.setOnKeyReleased(this);
    }
    
    public void set_cases(ObservableList<Case> cases){
        this.cases = cases;
        this.view.setItems(this.cases);
        this.view.refresh();
        this.field.setText("");
    }

    @Override
    public void handle(KeyEvent event) {
        String current = field.getText().toLowerCase();
        if(current.length()==0){
            view.setItems(cases);
            this.view.refresh();
        }else{
            ObservableList<Case> filtered = FXCollections.observableArrayList();
            for(Case cur_case:this.cases){
                if(cur_case.getCaseID().toLowerCase().contains(current)){
                    filtered.add(cur_case);
                }
            }
            this.view.setItems(filtered);
            this.view.refresh();
        }
    }    
}
