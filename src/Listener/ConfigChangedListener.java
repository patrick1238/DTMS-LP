/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Listener;

import Background_handler.Config;
import javafx.collections.ListChangeListener;

/**
 *
 * @author patri
 */
public class ConfigChangedListener implements ListChangeListener {
    
    Config config;
    
    public ConfigChangedListener(Config config) {
        this.config = config;
    }

    @Override
    public void onChanged(Change c) {
        this.config.save();
    }
    
}
