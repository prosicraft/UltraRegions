/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultraregions;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class URegion {
        public CuboidSelection sel;
        public String name = "Unknown Region";
        public boolean gamemode = false; // false = survival, true = creative
        public String greet = "You entered a URegion";
        public String greetingOthers = "You entered a claimed URegion";
        public String farewell = "You left a URegion";
        public String farewellOthers = "You left a claimed URegion";
        public String owner = "";
        public Location spawn = null;
        public boolean showMessages = true;
        
        public URegion (CuboidSelection cs, String s1, boolean b, String s2, String s3)
        {
                sel = cs;
                name = s1;
                gamemode = b;
                greet = s2;
                farewell = s3;
        }                
                
        public boolean isPlayerInside (Player p) {
                return sel.contains(p.getLocation());
        }
        
}
