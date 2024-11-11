package io.noks.perm.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zaxxer.hikari.HikariDataSource;

import io.noks.perm.enums.Ranks;
import io.noks.perm.managers.PlayerManager;

public class DBUtils  {
	private boolean connected = false;

	private final String address;
	private final String name;
	private final String username;
	private final String password;
	private HikariDataSource hikari;
	private final ExecutorService executorService;

	public DBUtils(String address, String name, String user, String password) {
		this.address = address;
		this.name = name;
		this.username = user;
		this.password = password;
		this.connectDatabase();
		this.executorService = (this.connected ? Executors.newCachedThreadPool() : null);
	}
	
	private void connectDatabase() {
		try {
			this.hikari = new HikariDataSource();
			this.hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
			this.hikari.addDataSourceProperty("serverName", this.address);
			this.hikari.addDataSourceProperty("port", "3306");
			this.hikari.addDataSourceProperty("databaseName", this.name);
			this.hikari.addDataSourceProperty("user", this.username);
			this.hikari.addDataSourceProperty("password", this.password);
			this.hikari.addDataSourceProperty("autoReconnect", Boolean.valueOf(true));
			this.hikari.addDataSourceProperty("cachePrepStmts", Boolean.valueOf(true));
			this.hikari.addDataSourceProperty("prepStmtCacheSize", Integer.valueOf(250));
			this.hikari.addDataSourceProperty("prepStmtCacheSqlLimit", Integer.valueOf(2048));
			this.hikari.addDataSourceProperty("useServerPrepStmts", Boolean.valueOf(true));
			this.hikari.addDataSourceProperty("cacheResultSetMetadata", Boolean.valueOf(true));
			this.hikari.setMaximumPoolSize(20);
			this.hikari.setConnectionTimeout(30000L);
			this.connected = true;
			createTable();
		} catch (Exception exception) {}
	}
	
	private void createTable() {
		if (!isConnected()) {
			return;
		}
		Connection connection = null;
		try {
			connection = this.hikari.getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS ranks(uuid VARCHAR(36) PRIMARY KEY, `rank` TEXT, UNIQUE(`uuid`));");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public HikariDataSource getHikari() {
		return this.hikari;
	}
	public boolean isConnected() {
		return this.connected;
	}
	public void close() {
		if(isConnected()) {
			if (executorService != null && !executorService.isShutdown()) {
				executorService.shutdown();
			}
			this.hikari.close();
			this.connected = false;
		}
	}

	public void loadPlayer(final UUID uuid) {
		if (!isConnected()) {
			new PlayerManager(uuid);
			return;
		}
		CompletableFuture.runAsync(() -> {
			Connection connection = null;
			try {
				connection = this.hikari.getConnection();
				final Ranks rank = this.loadPlayerRank(uuid, connection);
				new PlayerManager(uuid, rank); 
			} catch (SQLException ex) {
				ex.printStackTrace();
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
		}, executorService);
	}
	
	private Ranks loadPlayerRank(final UUID uuid, final Connection connection) throws SQLException {
		Ranks rank = Ranks.DEFAULT;
		try (PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM ranks WHERE uuid=?")) {
	        selectStatement.setString(1, uuid.toString());
	        try (ResultSet resultSet = selectStatement.executeQuery()) {
	            if (resultSet.next() && resultSet.getInt("count") == 0) {
	            	try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO ranks VALUES(?, ?)")) {
	                	insertStatement.setString(1, uuid.toString());
	                	insertStatement.setString(2, rank.getName());
	                	insertStatement.executeUpdate();
	                	insertStatement.close();
	    	        }
	            }
	            resultSet.close();
	        }
	        selectStatement.close();
	    }
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM ranks WHERE uuid=?")){
			statement.setString(1, uuid.toString());
			try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    rank = Ranks.getRankFromName(result.getString("rank"));
                }
                result.close();
            }
			statement.close();
		}
		return rank;
	}
	
	public void savePlayer(final PlayerManager pm) {
		if (!isConnected()) {
			pm.drop();
			return;
		}
		CompletableFuture.runAsync(() -> {
			Connection connection = null;
			try {
				connection = this.hikari.getConnection();
				final UUID uuid = pm.getPlayerUUID();
				this.savePlayerRank(uuid, pm.getRank(), connection);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				pm.drop();
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
		}, executorService);
	}
	
	private void savePlayerRank(final UUID uuid, final Ranks rank, final Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE ranks SET `rank`=? WHERE uuid=?")) {
        	statement.setString(1, rank.getName().toLowerCase());
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        }
    }
}
