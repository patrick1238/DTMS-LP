/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import Background_handler.Config;
import Background_handler.ErrorLog;
import Background_handler.ViewController;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;

/**
 *
 * @author patri
 */
public class Slice {

    private SimpleStringProperty Stain;
    private SimpleStringProperty Status;
    private SimpleStringProperty SliceType;
    private Button Print;
    private Button Remove;

    private Integer block_id;
    private Case corresponding_case;

    public Slice(String stain, String status, Integer block_id, String sliceType, Case corresponding_case) {
        this.Stain = new SimpleStringProperty(stain);
        this.Status = new SimpleStringProperty(status);
        this.SliceType = new SimpleStringProperty(sliceType);
        this.Print = new Button("Print");
        this.Remove = new Button("Remove");
        this.block_id = block_id;
        this.corresponding_case = corresponding_case;

        Remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                delete();
            }
        });

        Print.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                print_label();
            }
        });
    }

    public String getStain() {
        return this.Stain.get();
    }

    public void setStain(String stain_id) {
        this.Stain.set(stain_id);
    }

    public String getStatus() {
        return this.Status.get();
    }

    public void setStatus(String status) {
        this.Status.set(status);
    }
    
    public String getSliceType() {
        return this.SliceType.get();
    }

    public void setSliceType(String sliceType) {
        this.SliceType.set(sliceType);
    }

    public Button getPrint() {
        return this.Print;
    }

    public void setPrint(Button Print) {
        this.Print = Print;
    }

    public Button getRemove() {
        return this.Remove;
    }

    public void setRemove(Button Remove) {
        this.Remove = Remove;
    }

    public void print_label() {
        if (this.corresponding_case.getCaseID().length()==0) {
            ErrorLog.getErrorLog().createLogEntry(2, "CaseID empty");
        }else if(this.corresponding_case.getDiagnosis().length()==0){
            ErrorLog.getErrorLog().createLogEntry(2, "Diagnosis empty");
        }else {
            this.print();
            ErrorLog.getErrorLog().createLogEntry(0, "Case: " + this.corresponding_case.getCaseID() + ", Diagnosis: " + this.corresponding_case.getDiagnosis() + ", Block: " + Integer.toString(block_id) + ", Stain: " + this.getStain() +  " printed");
            this.Status.set("printed");
            this.corresponding_case.setStatus("progress");
            ViewController controller = ViewController.getViewController();
            controller.updateView(corresponding_case.getCaseID());
        }
    }
    
    private HashMap<String,String> prepareLabel(){
        HashMap<String,String> label = new HashMap<>();
        label.put("PrimKey", this.corresponding_case.getPrimaryKey());
        label.put("Block", Integer.toString(block_id));
        label.put("Year", this.corresponding_case.getYear());
        label.put("Stain", Stain.getValue());
        label.put("SliceType", this.SliceType.get());
        label.put("Diagnosis", this.corresponding_case.getDiagnosis());
        return label;
    }
    
    /**
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String generateEPLCommand() {
        HashMap<String,String> label = prepareLabel();
        String command = "";
        try (FileReader reader = new FileReader(Config.getConfig().get("template_path"))) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            String[] split;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("$")) {
                    split = line.split("\\$");
                    for (String splitted : split) {
                        if (label.containsKey(splitted)) {
                            splitted.replace("_", "");
                            line = line.replace("$" + splitted + "$", label.get(splitted));
                        }
                    }
                    command += line + "\n";
                } else {
                    command += line + "\n";
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Slice.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Slice.class.getName()).log(Level.SEVERE, null, ex);
        }
        return command;
    }

    public void print() {
        String command = generateEPLCommand();
        PrintService[] services = PrinterJob.lookupPrintServices();
        int pservice = 0;
        for (int i = 0; i < services.length; i++) {
            //services[i].getName();
            if (services[i].getName().toLowerCase().contains("zdesigner")) {
                pservice = i;
                break;
            }
        }
        System.out.println(services[pservice].getName());
        DocPrintJob job = services[pservice].createPrintJob();
        InputStream is = new ByteArrayInputStream(command.getBytes());
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc doc = new SimpleDoc(is, flavor, null);
 
        try {
            job.print(doc, null);
        } catch (PrintException ex) {
            Logger.getLogger(Slice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete() {
        this.corresponding_case.remove_slice(this, block_id);
        this.corresponding_case.setStatus("progress");
        ErrorLog.getErrorLog().createLogEntry(0, "Case: " + this.corresponding_case.getCaseID() + ", Block: " + Integer.toString(block_id) + ", Stain: " + this.getStain() + " deleted");
    }

    public String toString() {
        return this.Stain.get() + "," + Integer.toString(block_id) + "," + this.SliceType.get() + "," + this.Status.get();
    }

}
