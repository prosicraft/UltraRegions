/*
 * UltraRegions Security Listener
 */
package com.prosicraft.ultraregions;

import com.prosicraft.ultraregions.util.MLog;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author prosicraft
 */
public class URListener implements Listener
{

	public UltraRegions ur = null;
	public WorldEditInterface we = null;
	public Map<Player, GameMode> gmbackup = new HashMap<>();
	public Map<Player, GameMode> gmbackupa = new HashMap<>();
	public int savecount = 0;

	public URListener( UltraRegions prnt )
	{
		this.ur = prnt;
	}

	/**
	 * Try to hook into WorldEdit here
	 *
	 * @param event
	 */
	@EventHandler( priority = EventPriority.NORMAL )
	public void onPluginEnable( PluginEnableEvent event )
	{
		Plugin plug = event.getPlugin();

		if( plug != null && plug.getDescription().getName().equalsIgnoreCase( "WorldEdit" ) )
		{

			if( plug.getDescription().getName().equalsIgnoreCase( "WorldEdit" ) )
			{

				try
				{
					we = new WorldEditInterface( ur, ( WorldEditPlugin ) plug );
					MLog.i( "Hooked into WorldEdit" );
				}
				catch( NullPointerException nex )
				{
					MLog.e( "Can't bind to WorldEdit!" );
				}
				catch( Exception ex )
				{
					MLog.e( "Caught Fatal Error: " + ex.getMessage() );
				}

			}
		}
	}

	/**
	 * Check if event is permitted
	 *
	 * @param p The Player invoking this event
	 * @param l The location of modified entity
	 * @return true if player is permitted to do so
	 */
	public boolean isPermitted( Player p, Location l )
	{
		boolean blockInRegion = false;
		for( URegion reg : ur.regions )
		{
			if( !reg.sel.contains( l ) )
				continue;
			blockInRegion = true;
			if( !reg.owner.equalsIgnoreCase( p.getName() ) && !reg.owner.isEmpty() )
			{
				if( !p.hasPermission( "ultraregions.build.others" ) )
					return false;
			}
		}
		for( URegion reg : ur.autoassign )
		{
			if( !reg.sel.contains( l ) )
				continue;
			blockInRegion = true;
			if( !reg.owner.equalsIgnoreCase( p.getName() ) && !reg.owner.isEmpty() )
			{
				if( !p.hasPermission( "ultraregions.build.others" ) )
					return false;
			}
		}
		if( !blockInRegion )
		{
			if( !p.hasPermission( "ultraregions.build.everywhere" ) )
				return false;
		}

		return true;
	}

	/**
	 * Handle Block Placing Event
	 *
	 * @param e the event
	 */
	@EventHandler( priority = EventPriority.LOWEST )
	public void onBlockPlace( BlockPlaceEvent e )
	{
		if( !isPermitted( e.getPlayer(), e.getBlock().getLocation() ) )
			e.setCancelled( true );
	}

	/**
	 * Handle Block Breaking event
	 *
	 * @param e
	 */
	@EventHandler( priority = EventPriority.LOWEST )
	public void onBlockBreak( BlockBreakEvent e )
	{
		if( !isPermitted( e.getPlayer(), e.getBlock().getLocation() ) )
			e.setCancelled( true );
	}

	/**
	 * Handle Hanging (Painting and stuff) placment event
	 *
	 * @param e
	 */
	@EventHandler( priority = EventPriority.LOWEST )
	public void onHangingPlace( HangingPlaceEvent e )
	{
		if( e.getPlayer().getType() == EntityType.PLAYER )
		{
			if( !isPermitted( e.getPlayer(), e.getBlock().getLocation() ) )
				e.setCancelled( true );
		}
	}

	/**
	 * Handle Hanging (Painting and stuff) breaking event
	 *
	 * @param e
	 */
	@EventHandler( priority = EventPriority.LOWEST )
	public void onHangingBreak( HangingBreakByEntityEvent e )
	{
		if( e.getRemover().getType() == EntityType.PLAYER )
		{
			if( !isPermitted( ( Player ) e.getRemover(), e.getEntity().getLocation() ) )
				e.setCancelled( true );
		}
	}

	/**
	 * Handle bucket empty event
	 */
	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerBucketEmpty( PlayerBucketEmptyEvent e )
	{
		if( !isPermitted( e.getPlayer(), e.getBlockClicked().getLocation() ) )
			e.setCancelled( true );
	}

	/**
	 * Handle change of item-frames
	 *
	 * @param e
	 */
	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerInteraction( PlayerInteractEntityEvent e )
	{
		if( e.getRightClicked().getType() == EntityType.ITEM_FRAME )
		{
			if( !isPermitted( ( Player ) e.getPlayer(), e.getRightClicked().getLocation() ) )
				e.setCancelled( true );
		}
	}

	@EventHandler( priority = EventPriority.LOW )
	public void onPlayerJoin( PlayerJoinEvent event )
	{
		// print out notifications
		if( !ur.notifications.containsKey( event.getPlayer().getName() ) )
		{
			ur.notifications.put( event.getPlayer().getName(), Boolean.TRUE );
		}

		// check current gamemode on rejoin
		Player player = event.getPlayer();
		if( !player.hasPermission( "ultraregions.keepgamemode" ) )
		{
			boolean handledGamemode = false;

			// LOW PRIORITY
			// check if player is in any region
			for( URegion region : ur.regions )
			{
				if( region.sel.contains( player.getLocation() ) )
				{
					ur.setCreativeMode( player, region.gamemode );
					handledGamemode = true;
					break;
				}
			}

			// HIGH PRIORITY
			// .. check with autoassign regions
			for( URegion autoAssignRegion : ur.autoassign )
			{
				if( autoAssignRegion.sel.contains( player.getLocation() ) )
				{
					ur.setCreativeMode( player, autoAssignRegion.gamemode );
					handledGamemode = true;
					break;
				}
			}

			// if not in any region, assign world gamemode
			if( !handledGamemode )
			{
				ur.setCreativeMode( player, ur.getWorldGameMode( player ) );
			}
		}
	}

	/**
	 * Handle player movement
	 *
	 * @param event
	 */
	@EventHandler( priority = EventPriority.HIGHEST )
	public void onPlayerMove( PlayerMoveEvent event )
	{
		Player player = event.getPlayer();
		boolean ignoreGamemodeForPlayer = player.hasPermission( "ultraregions.keepgamemode" );
		boolean worldModeValid = false;

		// go through all normal regions
		for( URegion reg : ur.regions )
		{
			// Check if players switches between regions or region and world
			if( reg.sel.contains( event.getTo() ) != reg.sel.contains( event.getFrom() ) )
			{
				// now evaluate the target position
				if( reg.sel.contains( event.getTo() ) )
				{
					worldModeValid = true;
					player.sendMessage( MLog.real( reg.greet ) );

					if( !ignoreGamemodeForPlayer )
						ur.setCreativeMode( player, reg.gamemode );
				}
				else
				{
					player.sendMessage( MLog.real( reg.farewell ) );

					if( !ignoreGamemodeForPlayer )
						ur.setCreativeMode( player, ur.getWorldGameMode( player ) );
					worldModeValid = true;
				}
			}
			else if( reg.sel.contains( event.getTo() ) && reg.sel.contains( event.getFrom() ) )
			{
				// Prevent from hacking gamemode while walking on a region
				if( !ignoreGamemodeForPlayer )
				{
					if( player.getGameMode() != ( ( reg.gamemode ) ? GameMode.CREATIVE : GameMode.SURVIVAL ) )
					{
						ur.setCreativeMode( player, reg.gamemode );
					}
				}
				worldModeValid = true;
			}
		}

		// go through all auto assign regions
		for( URegion reg : ur.autoassign )
		{
			boolean showNotifications = ( reg.showMessages && ur.notifications.get( event.getPlayer().getName() ) );
			boolean isOwner = ( reg.owner.equalsIgnoreCase( event.getPlayer().getName() ) || reg.owner.equalsIgnoreCase( "noone" ) );

			if( reg.sel.contains( event.getTo() ) != reg.sel.contains( event.getFrom() ) )
			{
				// evaluate Target position
				if( reg.sel.contains( event.getTo() ) )
				{
					worldModeValid = true;
					if( showNotifications )
					{
						if( isOwner )
							player.sendMessage( MLog.real( reg.greet ) );
						else
							player.sendMessage( MLog.real( reg.greetingOthers ) );
					}

					if( !ignoreGamemodeForPlayer )
						ur.setCreativeMode( player, reg.gamemode );
				}
				else
				{
					if( showNotifications )
					{
						if( isOwner )
							player.sendMessage( MLog.real( reg.farewell ) );
						else
							event.getPlayer().sendMessage( MLog.real( reg.farewellOthers ) );
					}

					if( !ignoreGamemodeForPlayer )
						ur.setCreativeMode( player, ur.getWorldGameMode( player ) );
					worldModeValid = true;
				}
			}
			else if( reg.sel.contains( event.getTo() ) && reg.sel.contains( event.getFrom() ) )
			{
				// Prevent from hacking gamemode while walking on a region
				if( !ignoreGamemodeForPlayer )
				{
					if( player.getGameMode() != ( ( reg.gamemode ) ? GameMode.CREATIVE : GameMode.SURVIVAL ) )
					{
						ur.setCreativeMode( player, reg.gamemode );
					}
				}
				worldModeValid = true;
			}
		}

		// Check worlds if player is not in any of our regions
		if( !worldModeValid && !ignoreGamemodeForPlayer )
		{
			ur.setCreativeMode( player, ur.getWorldGameMode( player ) );
		}
	}

	/**
	 * Handle Player commands before going into bukkit procedures
	 *
	 * @param e
	 */
	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerCommand( PlayerCommandPreprocessEvent e )
	{
		if( e.isCancelled() )
			return;
		if( e.getMessage().substring( 1 ).equalsIgnoreCase( this.ur.autoAssignCommand ) )
		{
			MLog.i( "Auto assigning Plot to player '" + e.getPlayer().getName() + "'" );
			ur.assignPlot( e.getPlayer() );
			// catch
			e.setCancelled( true );
		}
	}
}
