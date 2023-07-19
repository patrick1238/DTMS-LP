/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Background_handler;

import Listener.ConfigChangedListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author patri
 */
public class Config {
    
    private static Config config;

    HashMap<String, String> configHandler;
    ObservableList<String> configObserver;
    private String configPath = "C:\\Users\\patri\\OneDrive\\Dokumente\\Patho\\Literatur\\Own work\\Patrick\\Diss\\Tools\\DTMS-LP\\Resources\\config\\config.txt";
    
    
    private Config() {
        BufferedReader br = null;
        try {
            //configPath = this.getClass().getClassLoader().getResource("config/config.txt").getFile();
            this.configHandler = new HashMap<>();
            configObserver = FXCollections.observableArrayList();
            String[] splitted;
            br = new BufferedReader(new FileReader(configPath));
            String line = br.readLine();
            while (line != null) {
                if (!line.startsWith("#") && line.length() > 0) {
                    splitted = line.split("=");
                    this.configHandler.put(splitted[0], splitted[1]);
                }
                this.configObserver.add(line);
                line = br.readLine();
            }   this.configObserver.addListener(new ConfigChangedListener(this));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static Config getConfig() {
        if(config==null){
            config = new Config();
        }
        return config;
    }

    public String get(String key) {
        return configHandler.get(key);
    }

    public void replace(String key, String value) {
        int index = -1;
        for(String line:this.configObserver){
            index += 1;
            if(line.contains(key)){
                break;
            }
        }
        this.configObserver.set(index, this.configObserver.get(index).replace(this.configHandler.get(key), value));
        this.configHandler.replace(key, value);
    }

    public boolean containsKey(String key) {
        return this.configHandler.containsKey(key);
    }

    public boolean containsValue(String value) {
        return this.configHandler.containsValue(value);
    }
    
    public String getNewImageID(){
        int id = Integer.parseInt(this.configHandler.get("LastImageID")) + 1;
        this.replace("LastImageID", Integer.toString(id));
        return this.configHandler.get("AddressID")+Integer.toString(id);
    }

    public String getControllitem(String type, String identifier){
        String representation = "";
        HashMap<String, String> controlItems = new HashMap();
        String[] header = this.get(type + "Header").split(",");
        String[] controler = this.get(type + "ControlItems").split(",");
        for (int i = 0; i < header.length; i++) {
            controlItems.put(header[i], controler[i]);
        }
        return controlItems.get(identifier);
    }
    
    public void save(){
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(this.configPath,false));
            out.write(this.toString());
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public String toString() {
        return String.join("\n", this.configObserver);
    }
}
