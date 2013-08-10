/*
 * UltraRegions World
 */
package com.prosicraft.ultraregions;

import org.bukkit.GameMode;

/**
 *
 * @author kaipower
 * @edited by prosicraft
 */
public class UWorld
{

	/**
	 * World name
	 */
	public String name = "";

	/**
	 * Gamemode of this world true = creative, false = survival
	 */
	public boolean gamemode = false;

	/**
	 * Default plot gamemode true = creative, false = survival
	 */
	public boolean defaultPlotGamemode = true;

	/**
	 * enable global build
	 */
	public boolean enableGlobalBuild = false;

	/**
	 * AutoAssign command for this world
	 */
	public String autoAssignCommand = "givemeaplot";

	/**
	 * Get default plot gamemode
	 *
	 * @return
	 */
	public boolean getDefaultPlotGamemode()
	{
		return defaultPlotGamemode;
	}

	/**
	 * Set the default plot gamemode
	 *
	 * @param defaultPlotGamemode
	 */
	public void setDefaultPlotGamemode( boolean defaultPlotGamemode )
	{
		this.defaultPlotGamemode = defaultPlotGamemode;
	}

	/**
	 * Get World gamemode
	 *
	 * @return
	 */
	public boolean getGameModeBoolean()
	{
		return gamemode;
	}

	/**
	 * Get World gamemode as GameMode
	 */
	public GameMode getGameMode()
	{
		return ( gamemode ) ? GameMode.CREATIVE : GameMode.SURVIVAL;
	}

	/**
	 * Set the world gamemode
	 *
	 * @param gamemode
	 */
	public void setGameMode( boolean gamemode )
	{
		this.gamemode = gamemode;
	}

	/**
	 * Check whether global build is enabled or not
	 *
	 * @return
	 */
	public boolean isGlobalBuild()
	{
		return enableGlobalBuild;
	}

	/**
	 * Enable global build or not
	 *
	 * @param enableGlobalBuild
	 */
	public void setGlobalBuild( boolean enableGlobalBuild )
	{
		this.enableGlobalBuild = enableGlobalBuild;
	}

	/**
	 * Set the world name
	 *
	 * @param name
	 */
	public void setName( String name )
	{
		this.name = name;
	}

	/**
	 * Get the world name
	 */
	public String getName()
	{
		return this.name;
	}
}
