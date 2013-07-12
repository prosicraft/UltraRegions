/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultraregions;

import com.prosicraft.ultraregions.util.MLog;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author prosicraft
 */
public class WorldEditInterface {
    
    public WorldEditPlugin worldedit = null; 
    public UltraRegions parent = null;
    
    public WorldEditInterface (UltraRegions handle, WorldEditPlugin we) {
        parent = handle;
    }            
    
    public boolean initWorldEdit () {   // this function is because of a may-be wrong plugin load priority
        if ( worldedit != null ) return true; // it's already loaded
        
        Plugin[] plugs = parent.getServer().getPluginManager().getPlugins();
        if ( plugs == null || plugs.length == 0 )
            return false;
       
        for ( int i = 0; i < plugs.length; i++) {
            if ( plugs[i].getDescription().getName().equalsIgnoreCase("WorldEdit") ) {
                try {
                    this.worldedit = (WorldEditPlugin) plugs[i];                
                    MLog.i ("Hooked into WorldEdit");
                    return true;
                } catch (NullPointerException nex) {
                    MLog.e ("Can't bind to WorldEdit!");
                } catch (Exception ex) {
                    MLog.e ("Caught Fatal Error: " + ex.getMessage());
                }
                return false;
            }
        }            
        return false;
    }
    
    public boolean isRegionSelected (Player p) {                        
        
        try {
            
            if ( !initWorldEdit() ) {
                if ( p.isOp() )
                    p.sendMessage(ChatColor.YELLOW + "Warning: Connection to worldedit is not up.");
                return false;
            }
            
            BukkitPlayer bp = new BukkitPlayer (worldedit, worldedit.getServerInterface(), p);           
            if( worldedit == null )
            {
                p.sendMessage(ChatColor.DARK_GRAY + "[UR] " + ChatColor.YELLOW + "Connection to worldedit is not up! (boot error)");
                return false;
            }            
            else if( worldedit.getSession(p) == null )
            {
                p.sendMessage(ChatColor.DARK_GRAY + "[UR] " + ChatColor.YELLOW + "Connection to worldedit is not up! (Session error)");
                return false;
            }
            else
                return worldedit.getSession(p).isSelectionDefined(bp.getWorld()); 
            
        } catch (Exception ex) {
            
            MLog.e ("Error while checking if Selection is Defined: ");
            ex.printStackTrace(System.out);
            return false;
            
        }        
    }
    
    public CuboidSelection getRegion (Player p) {
        
        try {
            com.sk89q.worldedit.Vector vp = toSk89qVector(worldedit.getSelection(p).getMinimumPoint().toVector());
            com.sk89q.worldedit.Vector vs = toSk89qVector(worldedit.getSelection(p).getMaximumPoint().toVector());
            
            CuboidSelection cs = new CuboidSelection(p.getWorld(), vp, vs);
            return cs;                        
            
        } catch (Exception ex) {
            
            MLog.e ("Error while getting Region: " + ex.getStackTrace().toString());
            return null;
            
        }
        
    }
    
    public com.sk89q.worldedit.Vector toSk89qVector (org.bukkit.util.Vector v) {
        return new com.sk89q.worldedit.Vector(v.getX(), v.getY(), v.getZ());        
    }
}
