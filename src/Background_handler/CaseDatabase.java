/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Background_handler;

import Objects.Case;
import Objects.Slice;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author patri
 */
public class CaseDatabase {

    private static CaseDatabase database;

    HashMap<String, Case> cases;
    ObservableList<Case> case_list;

    private CaseDatabase() {
        try {
            Config config = Config.getConfig();
            BufferedReader br = null;
            this.cases = new HashMap<>();
            this.case_list = FXCollections.observableArrayList();
            String[] splitted;
            br = new BufferedReader(new FileReader(config.get("database_path")));
            String line = br.readLine();
            Case cur;
            while (line != null) {
                if (line.length() > 0) {
                    splitted = line.split(";");
                    cur = new Case(splitted[0], splitted[1]);
                    if (!splitted[2].equals("pending")) {
                        HashMap<Integer, ObservableList<Slice>> biopsy_material = load_biopsy_material(cur);
                        if (biopsy_material.isEmpty()) {
                            cur.add_block(1,"nod");
                        } else {
                            cur.setStatus(splitted[2]);
                            cur.set_biopsy_material(biopsy_material);
                        }
                    } else {
                        cur.add_block(1,"nod");
                    }
                    this.cases.put(splitted[0], cur);
                    this.case_list.add(cur);
                }
                line = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CaseDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CaseDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static CaseDatabase getCaseDatabase() {
        if (database == null) {
            database = new CaseDatabase();
        }
        return database;
    }
    
    public void add_case(Case new_case){
        this.cases.put(new_case.getCaseID(), new_case);
        this.case_list.add(new_case);
        ViewController.getViewController().updateView("MainView");
    }

    public Case get_case(String case_id) {
        return this.cases.get(case_id);
    }
    
    public void remove_case(Case old_case){
        this.case_list.remove(old_case);
        this.cases.remove(old_case.getCaseID());
        ViewController.getViewController().updateView("MainView");
    }
    
    public boolean contains_case(String case_id){
        return this.cases.containsKey(case_id);
    }

    public ObservableList<Case> get_all_cases() {
        return case_list;
    }

    public ArrayList<String> get_all_case_identifier() {
        return new ArrayList<String>(this.cases.keySet());
    }

    public ArrayList<String> get_all_diagnosis() {
        ArrayList<String> diagnosis = new ArrayList<>();
        for (Case cur : this.cases.values()) {
            if (!diagnosis.contains(cur.getDiagnosis())) {
                diagnosis.add(cur.getDiagnosis());
            }
        }
        return diagnosis;
    }

    private HashMap<Integer, ObservableList<Slice>> load_biopsy_material(Case cur) {
        HashMap<Integer, ObservableList<Slice>> biopsy_material = new HashMap<Integer, ObservableList<Slice>>();
        try {
            Config config = Config.getConfig();
            BufferedReader br = null;
            String[] splitted;
            File cur_case = new File(config.get("cases_path") + cur.getCaseID() + ".csv");
            if (cur_case.exists()) {
                br = new BufferedReader(new FileReader(cur_case.getAbsolutePath()));
                String line = br.readLine();
                Integer block_id;
                String stain, status, sliceType;
                while (line != null) {
                    if (line.length() > 0) {
                        splitted = line.split(",");
                        stain = splitted[0];
                        block_id = Integer.parseInt(splitted[1]);
                        sliceType = splitted[2];
                        System.out.println(cur.getCaseID());
                        status = splitted[3];
                        if (!biopsy_material.containsKey(block_id)) {
                            biopsy_material.put(block_id, FXCollections.observableArrayList());
                        }
                        biopsy_material.get(block_id).add(new Slice(stain, status, block_id, sliceType, cur));
                    }
                    line = br.readLine();
                }
                br.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CaseDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CaseDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return biopsy_material;
    }
    
    public String getCaseIDSuggestion(){
        String lastCaseID = Config.getConfig().get("LastID");
        int imageid_int = new Scanner(lastCaseID).useDelimiter("\\D+").nextInt();
        String newid = Integer.toString(imageid_int+1);
        return lastCaseID.replaceFirst(Integer.toString(imageid_int), newid);
        
    }

    public void close() {
        BufferedWriter writer = null;
        try {
            Config config = Config.getConfig();
            File logFile = new File(config.get("database_path"));

            writer = new BufferedWriter(new FileWriter(logFile));
            String output = "";
            for (Case cur : this.case_list) {
                output = output + cur.toString() + "\n";
            }
            writer.write(output);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
    }
}
