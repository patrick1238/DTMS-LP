/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import Background_handler.CaseDatabase;
import Background_handler.Config;
import Background_handler.ErrorLog;
import Background_handler.StainDatabase;
import Background_handler.ViewController;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 *
 * @author patri
 */
public class Case {

    private SimpleStringProperty CaseID;
    private SimpleStringProperty Diagnosis;
    private SimpleStringProperty Status;
    private String primKey;
    private String year;
    private Button Remove;
    private HashMap<Integer, ObservableList<Slice>> biopsy_material;

    public Case(String id, String diagnosis) {
        this.CaseID = new SimpleStringProperty(id);
        HashMap<String,String> identifier = parseCaseID(id);
        this.primKey = identifier.get("prim_key");
        this.year = identifier.get("year");
        this.Diagnosis = new SimpleStringProperty(diagnosis);
        this.Status = new SimpleStringProperty("pending");
        this.biopsy_material = new HashMap<>();
        this.Remove = new Button("Remove");
        Remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                delete();
            }
        });
    }

    public Case(String id, String diagnosis,String status, HashMap<Integer, ObservableList<Slice>> biopsy_material) {
        this.CaseID = new SimpleStringProperty(id);
        HashMap<String,String> identifier = parseCaseID(id);
        this.primKey = identifier.get("prim_key");
        this.year = identifier.get("year");
        this.Diagnosis = new SimpleStringProperty(diagnosis);
        this.Status = new SimpleStringProperty(status);
        this.biopsy_material = biopsy_material;
        this.Remove = new Button("Remove");
        Remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                delete();
            }
        });
    }
    
    public String getCaseID() {
        return this.CaseID.get();
    }

    public void setCaseID(String id) {
        this.CaseID.set(id);
        HashMap<String,String> identifier = parseCaseID(id);
        this.primKey = identifier.get("prim_key");
        this.year = identifier.get("year");
    }

    public String getDiagnosis() {
        return this.Diagnosis.get();
    }

    public void setDiagnosis(String diagnosis) {
        this.Diagnosis.set(diagnosis);
    }
    
    public String getPrimaryKey(){
        return this.primKey;
    }
    
    public String getYear(){
        return this.year;
    }

    public String getStatus() {
        return this.Status.get();
    }

    public void setStatus(String status) {
        this.Status.set(status);
    }
    
    public Button getRemove(){
        return this.Remove;
    }
    
    public void setRemove(Button Remove){
        this.Remove = Remove;
    }

    public HashMap<Integer, ObservableList<Slice>> get_biopsy_material() {
        return this.biopsy_material;
    }

    public void set_biopsy_material(HashMap<Integer, ObservableList<Slice>> material) {
        this.biopsy_material = material;
    }

    public void add_block(Integer block_id, String block_type) {
        ArrayList<Slice> current_block = new ArrayList<>();
        StainDatabase stains = StainDatabase.get_Stain_Database();
        String diagnosis = this.Diagnosis.get();
        for (String stain : stains.get_Stains(this.Diagnosis.get())) {
            current_block.add(new Slice(stain, "pending", block_id, block_type, this));
        }
        this.biopsy_material.put(block_id, FXCollections.observableArrayList(current_block));
    }

    public void remove_block(Integer block_id, String possible_blocktype) {
        if (this.biopsy_material.keySet().size() > 1) {
            this.biopsy_material.remove(block_id);
        } else {
            this.biopsy_material.remove(block_id);
            add_block(1,possible_blocktype);
            this.Status.set("generated");
        }
    }

    public void add_slice(String stain, Integer block_id, String sliceType) {
        if (this.biopsy_material.containsKey(block_id)) {
            this.biopsy_material.get(block_id).add(new Slice(stain, "pending", block_id, sliceType, this));
        }
        this.Status.set("progress");
    }

    public void remove_slice(Slice slice, Integer block_id) {
        if (this.biopsy_material.containsKey(block_id)) {
            this.biopsy_material.get(block_id).remove(slice);
        }
        ViewController controller = ViewController.getViewController();
        controller.updateView(this.CaseID.get());
    }
    
    public void delete(){
        CaseDatabase database = CaseDatabase.getCaseDatabase();
        database.remove_case(this);
        if(!this.Status.get().equals("pending")){
            File logFile = new File(Config.getConfig().get("cases_path") + this.CaseID.get() + ".csv");
            if(logFile.exists()){
                logFile.delete();
            }
        }
        ErrorLog.getErrorLog().createLogEntry(0,"Case: " + this.getCaseID() + " deleted");
    }

    public boolean observe_case_csv() {
        Config config = Config.getConfig();
        File logFile = new File(config.get("cases_path") + this.CaseID.get() + ".csv");
        if (!logFile.exists()) {
            try {
                if(!logFile.getParentFile().exists()){
                    logFile.getParentFile().mkdirs();
                }
                logFile.createNewFile();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void update_case_csv() {
        BufferedWriter writer = null;
        try {
            Config config = Config.getConfig();
            File logFile = new File(config.get("cases_path") + this.CaseID.get() + ".csv");

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(generate_case_log());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    private String generate_case_log() {
        String log = "";
        for (Integer block_id : this.biopsy_material.keySet()) {
            for (Slice slice : this.biopsy_material.get(block_id)) {
                log = log + slice.toString() + "\n";
            }
        }
        return log;
    }
    
    public void update_case(){
        int pending = 0;
        for(Integer block_id:this.biopsy_material.keySet()){
            for(Slice slice:biopsy_material.get(block_id)){
                if(slice.getStatus().equals("pending")){
                    pending += 1;
                }
            }
        }
        if(pending==0){
            this.Status.set("finished");
        }
        update_case_csv();
    }
    
    private HashMap<String,String> parseCaseID(String caseID){
        HashMap<String,String> identifier = new HashMap<>();
        String[] splitted = caseID.split("-");
        identifier.put("prim_key", splitted[0]);
        try{
            identifier.put("year", splitted[1]);
        }catch(IndexOutOfBoundsException ex){
            Logger.getLogger(getClass().getName()).warning("Could not parse year from caseID='"+caseID+"' TODO: handle this error, setting year to 0");
            identifier.put("year", "0");
        }
        return identifier;
    }
    
    public String toString(){
        String output = this.CaseID.get()+";"+this.Diagnosis.get()+";"+this.Status.get();
        return output;
    }
}
