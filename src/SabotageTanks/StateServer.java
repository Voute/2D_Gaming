/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import SabotageTanks.GraphicObjects.GameObject;
import SabotageTanks.GraphicObjects.Shell;
import SabotageTanks.GraphicObjects.Tank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ytokmakov
 */
public final class StateServer extends State {
    
    List<Tank> tankList;
    List<Shell> shellList;
    
    public StateServer()
    {
        tankList = Collections.synchronizedList(new ArrayList<Tank>());
        shellList = Collections.synchronizedList(new ArrayList<Shell>());
    }

    public ArrayList<GameObject> getObjectsToDraw() {
        
        ArrayList<GameObject> returnArray = new ArrayList<GameObject>();
        returnArray.addAll(tankList);
        returnArray.addAll(shellList);
        return returnArray;
        
    }
    
}
