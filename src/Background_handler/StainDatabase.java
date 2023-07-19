/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Background_handler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author patri
 */
public class StainDatabase {
    
    private static StainDatabase staindatabase;

    private HashMap<String, String[]> stains;

    private StainDatabase() {
        try {
            Config config = Config.getConfig();
            BufferedReader br = null;
            this.stains = new HashMap<>();
            String[] splitted;
            br = new BufferedReader(new FileReader(config.get("staining_path")));
            String line = br.readLine();
            while (line != null) {
                if (line.length() > 0 && !line.startsWith("#")) {
                    splitted = line.split("=");
                    this.stains.put(splitted[0], splitted[1].split(","));
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

    public static StainDatabase get_Stain_Database() {
        if (staindatabase == null) {
            staindatabase = new StainDatabase();
        }
        return staindatabase;
    }

    public String[] get_Stains(String diagnosis){
        if(this.stains.containsKey(diagnosis)){
            return this.stains.get(diagnosis);
        }else{
            return this.stains.get("undefined");
        }
    }
    
    public ArrayList<String> get_all_possible_diagnosis(){
        return new ArrayList<String>(this.stains.keySet());
    }
    
    public ArrayList<String> get_all_possible_stains(){
        ArrayList<String> all_stains = new ArrayList<>();
        for(String key:this.stains.keySet()){
            for(String stain:this.stains.get(key)){
                if(!all_stains.contains(stain)){
                    all_stains.add(stain);
                }
            }
        }
        return all_stains;
    }
}
