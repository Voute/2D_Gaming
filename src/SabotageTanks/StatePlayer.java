/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import SabotageTanks.GraphicObjects.Shell;
import SabotageTanks.GraphicObjects.Tank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ytokmakov
 */
public final class StatePlayer extends State {
    
    Tank tank;
    List<Shell> shellList;
    
    public StatePlayer()
    {
        shellList = Collections.synchronizedList(new ArrayList<Shell>());
    }
    
}
