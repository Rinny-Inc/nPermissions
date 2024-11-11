package io.noks.perm;

import org.bukkit.plugin.java.JavaPlugin;

import io.noks.perm.commands.RankCommand;
import io.noks.perm.database.DBUtils;
import io.noks.perm.listeners.ChatListener;
import io.noks.perm.listeners.PlayerListener;

public class Main extends JavaPlugin {
	
	private static Main instance;
	public static Main getInstance() {
		return instance;
	}
	private DBUtils database;
	
	@Override
	public void onEnable() {
		instance = this;
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		
		this.database = new DBUtils(getConfig().getString("DATABASE.ADDRESS"), getConfig().getString("DATABASE.NAME"), getConfig().getString("DATABASE.USER"), getConfig().getString("DATABASE.PASSWORD"));
		
		registerListeners();
		registerCommands();
	}
	
	@Override
	public void onDisable() {
	}
	
	private void registerListeners() {
		new PlayerListener(this);
		new ChatListener(this);
	}
	
	private void registerCommands() {
		new RankCommand(this);
	}
	
	public DBUtils getDataBase() {
		return this.database;
	}
}
