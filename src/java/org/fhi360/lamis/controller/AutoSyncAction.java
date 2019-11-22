/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller;

import java.util.Date;
import org.fhi360.lamis.service.SyncService;


/**
 *
 * @author user10
 */
  public class AutoSyncAction extends Thread{
    
    public AutoSyncAction(String name) {
        super(name);
    }

    @Override
    public void run() {
        try {
          // try doing the sync asynchronously ...
           System.out.println ("Thread " + 
                  Thread.currentThread().getId() + 
                  " is running at "+ new Date());
                SyncService syncService = new SyncService();
                syncService.startTransaction();
        }catch(Exception ex){
            ex.printStackTrace();
            
        }
    }
}