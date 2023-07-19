/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Windowcontroller;

import Background_handler.CaseDatabase;
import Background_handler.Config;
import Background_handler.ErrorLog;
import Background_handler.StainDatabase;
import Background_handler.ViewController;
import EventHandler.AutoCompleteHandler;
import Interfaces.DTMSView;
import Objects.Case;
import Objects.Slice;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author patri
 */
public class Case_viewController implements Initializable, DTMSView {

    @FXML
    private ComboBox<String> Add_Box;
    @FXML
    private ComboBox<String> Diagnose_Box;
    @FXML
    private ComboBox<Integer> Block_Box;
    @FXML
    private Button Add_Block;
    @FXML
    private Button print_button;
    @FXML
    private TextField CaseField;
    @FXML
    private Button Remove_Block;
    @FXML
    private TableView<Slice> Slice_table;
    @FXML
    private Label Slice_label;
    @FXML
    private Button add_slice;
    @FXML
    private TextField Block_field;
    @FXML
    private ComboBox<String> BlockType;

    CaseDatabase database;
    StainDatabase stains;
    Case cur_case;
    String viewid;
    boolean changed;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.database = CaseDatabase.getCaseDatabase();
        this.stains = StainDatabase.get_Stain_Database();
        this.BlockType.getItems().add("nod");
        this.BlockType.getItems().add("en");
        this.BlockType.getItems().add("st");
        this.BlockType.getItems().add("km");
        this.BlockType.getSelectionModel().selectFirst();
        set_table();
        set_listener();
        this.changed = false;
        this.add_slice.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                add_slice_clicked(new ActionEvent());
            }
        });
    }

    @FXML
    private void Add_Blocked_clicked(ActionEvent event) {
        Integer new_block = Integer.parseInt(this.Block_field.getText());
        if (!this.cur_case.get_biopsy_material().containsKey(new_block)) {
            this.cur_case.setDiagnosis(this.Diagnose_Box.getEditor().getText());
            this.cur_case.add_block(new_block,this.BlockType.getSelectionModel().getSelectedItem());
            ObservableList<Integer> blocks = FXCollections.observableArrayList(cur_case.get_biopsy_material().keySet());
            this.Block_Box.setItems(blocks);
            this.Block_Box.getSelectionModel().select(new_block);
            this.cur_case.setStatus("progress");
            ErrorLog.getErrorLog().createLogEntry(0, "Case: " + this.cur_case.getCaseID() + ", Block: " + Integer.toString(new_block) + " added");
        } else {
            ErrorLog.getErrorLog().createLogEntry(2, "Block " + Integer.toString(new_block) + " still exist");
        }
    }

    @FXML
    private void remove_clicked(ActionEvent event) {
        Integer block = this.Block_Box.getSelectionModel().getSelectedItem();
        int select = this.Block_Box.getSelectionModel().getSelectedIndex();
        this.cur_case.remove_block(block,this.BlockType.getSelectionModel().getSelectedItem());
        ObservableList<Integer> blocks = FXCollections.observableArrayList(cur_case.get_biopsy_material().keySet());
        select = Math.min(select, blocks.size() - 1);
        this.Block_Box.setItems(blocks);
        if (blocks.size() == 1 || select == 0) {
            this.Block_Box.getSelectionModel().selectFirst();
        } else {
            this.Block_Box.getSelectionModel().select(select);
        }
    }

    @FXML
    private void add_slice_clicked(ActionEvent event) {
        String stain = Add_Box.getEditor().getText();
        Integer block = this.Block_Box.getSelectionModel().getSelectedItem();
        String sliceType = this.BlockType.getSelectionModel().getSelectedItem();
        this.cur_case.add_slice(stain, block,sliceType);
        ObservableList<Slice> selected_stains = FXCollections.observableArrayList(cur_case.get_biopsy_material().get(block));
        Slice_table.setItems(selected_stains);
        this.cur_case.setStatus("progress");
    }

    @FXML
    private void print_clicked(ActionEvent event) {
        HashMap<Integer, ObservableList<Slice>> slices = this.cur_case.get_biopsy_material();
        for (Integer block : slices.keySet()) {
            for (Slice slice : slices.get(block)) {
                if (slice.getStatus().equals("pending")) {
                    slice.print_label();
                }
            }
        }
        ViewController.getViewController().closeView(cur_case.getCaseID());
    }

    public void view_case(Case selected_case) {

        this.cur_case = selected_case;
        String case_id = cur_case.getCaseID();

        CaseField.setText(case_id);

        ObservableList<String> diagnosis = FXCollections.observableArrayList(StainDatabase.get_Stain_Database().get_all_possible_diagnosis());
        this.Diagnose_Box.setItems(diagnosis);
        this.Diagnose_Box.getSelectionModel().select(cur_case.getDiagnosis());

        ObservableList<String> possible_stains = FXCollections.observableArrayList(this.stains.get_all_possible_stains());
        this.Add_Box.setItems(possible_stains);
        this.Add_Box.getSelectionModel().selectFirst();

        ObservableList<Integer> blocks = FXCollections.observableArrayList(cur_case.get_biopsy_material().keySet());
        this.Block_Box.setItems(blocks);
        this.Block_Box.getSelectionModel().selectFirst();
        this.viewid = cur_case.getCaseID();

        set_Handler();
    }

    private void set_Handler() {
        new AutoCompleteHandler(this.Diagnose_Box);
        new AutoCompleteHandler(this.Add_Box);
    }

    private void set_table() {
        Slice_table.getColumns().clear();
        TableColumn stain = new TableColumn("Stain");
        stain.setCellValueFactory(new PropertyValueFactory<Slice, String>("Stain"));
        Slice_table.getColumns().add(stain);
        TableColumn sliceType = new TableColumn("Slicetype");
        sliceType.setCellValueFactory(new PropertyValueFactory<Slice, String>("SliceType"));
        Slice_table.getColumns().add(sliceType);
        TableColumn status = new TableColumn("Status");
        status.setCellValueFactory(new PropertyValueFactory<Slice, String>("Status"));
        Slice_table.getColumns().add(status);
        TableColumn print = new TableColumn("Print");
        print.setCellValueFactory(new PropertyValueFactory<Slice, Button>("Print"));
        Slice_table.getColumns().add(print);
        TableColumn remove = new TableColumn("Remove");
        remove.setCellValueFactory(new PropertyValueFactory<Slice, Button>("Remove"));
        Slice_table.getColumns().add(remove);
    }

    private void set_listener() {
        this.Block_Box.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (newValue != null) {
                    ObservableList<Slice> selected_stains = FXCollections.observableArrayList(cur_case.get_biopsy_material().get(newValue));
                    Slice_table.setItems(selected_stains);
                }
            }
        });
        this.CaseField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    if (!changed) {
                        File old_file = new File(Config.getConfig().get("cases_path") + cur_case.getCaseID() + ".csv");
                        old_file.delete();
                    }
                    cur_case.setCaseID(CaseField.getText());
                }
            }
        });
        this.Diagnose_Box.getEditor().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    cur_case.setDiagnosis(Diagnose_Box.getEditor().getText());
                }
            }
        });
    }

    @Override
    public String getViewID() {
        return this.viewid;
    }

    @Override
    public void updateView() {
        Slice_table.setItems(this.cur_case.get_biopsy_material().get(this.Block_Box.getSelectionModel().getSelectedItem()));
        Slice_table.refresh();
    }

    @Override
    public void close() {
        if (changed) {
            cur_case.observe_case_csv();
        }
        cur_case.update_case();
        Stage stage = (Stage) Block_Box.getScene().getWindow();
        stage.close();
    }

}
