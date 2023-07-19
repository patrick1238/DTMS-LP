/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EventHandler;

import Background_handler.ViewController;
import javafx.event.Event;
import javafx.event.EventHandler;

/**
 *
 * @author patri
 */
public class CloseRequest implements EventHandler{
    
    private String viewid;
    
    public CloseRequest(String viewid){
        this.viewid = viewid;
    }

    @Override
    public void handle(Event event) {
        ViewController controller = ViewController.getViewController();
        controller.closeView(viewid);
    }
    
}
