/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Background_handler;

import Interfaces.DTMSView;
import java.util.HashMap;

/**
 *
 * @author patri
 */
public class ViewController {
    
    private static ViewController controller;
    private HashMap<String,DTMSView> views;
    
    
    
    private ViewController(){        
        views = new HashMap<>();
    }
    
    public static ViewController getViewController() {
        if (controller == null) {
            controller = new ViewController();
        }
        return controller;
    }
    
    public boolean addView(DTMSView view){
        if(!views.containsKey(view.getViewID())){
            this.views.put(view.getViewID(), view);
            return true;
        }else{
            return false;
        }
    }
    
    public void updateView(String viewid){
        this.views.get(viewid).updateView();
    }
    
    public void closeView(String viewid){
        this.views.get(viewid).close();
        this.views.remove(viewid);
        if(!viewid.equals("MainView")){
            views.get("MainView").updateView();
        }
    }
    
    public void closeChildViews(){
        for(DTMSView view:views.values()){
            if(!view.getViewID().equals("MainView")){
                view.close();
            }
        }
    }
    
}
