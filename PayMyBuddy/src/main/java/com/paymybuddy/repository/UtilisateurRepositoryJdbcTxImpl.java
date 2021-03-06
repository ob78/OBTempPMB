package com.paymybuddy.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerJDBC;

/**
 * Class managing the data persistence for the user.
 */
public class UtilisateurRepositoryJdbcTxImpl implements IUtilisateurRepository {

	private static final Logger logger = LoggerFactory.getLogger(UtilisateurRepositoryJdbcTxImpl.class);

	private RepositoryTxManagerJDBC repositoryTxManager = null;

	public UtilisateurRepositoryJdbcTxImpl(RepositoryTxManagerJDBC repositoryTxManager) {
		this.repositoryTxManager = repositoryTxManager;
	}
	
	/**
	 * Add a user in the repository.
	 * 
	 * @param Utilisateur The user to add
	 */
	@Override
	public void create(Utilisateur utilisateur) {

		final String REQUEST_CREATE = "INSERT INTO utilisateur (email, password, solde) VALUES (?,?,?)";
		
		Connection postgreCon = repositoryTxManager.getConnection();
		try (
			PreparedStatement ps = postgreCon.prepareStatement(REQUEST_CREATE)) {

			ps.setString(1, utilisateur.getEmail());
			ps.setString(2, utilisateur.getPassword());
			ps.setDouble(3, utilisateur.getSolde());
			int updateRowCount = ps.executeUpdate();

			if (updateRowCount != 1) {
				logger.error("Error in Utilisateur insertion");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Update a user in the repository.
	 * 
	 * @param Utilisateur The user to update
	 */
	@Override
	public void update(Utilisateur utilisateur) {

		final String REQUEST_UPDATE = "UPDATE utilisateur SET password=?, solde=? WHERE email=?";

		Connection postgreCon = repositoryTxManager.getConnection();
		try (
			PreparedStatement psUtilisateur = postgreCon.prepareStatement(REQUEST_UPDATE);) {
			psUtilisateur.setString(1, utilisateur.getPassword());
			psUtilisateur.setDouble(2, utilisateur.getSolde());
			psUtilisateur.setString(3, utilisateur.getEmail());

			int updateRowCountUtilisateur = psUtilisateur.executeUpdate();

			if (updateRowCountUtilisateur != 1) {
				logger.error("Error in Utilisateur update");
			}
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Read a user from the repository.
	 * 
	 * @param email The email of the user to read
	 * 
	 * @return The user read
	 */
	@Override
	public Utilisateur read(String email) {

		final String REQUEST_READ_UTILISATEUR = "SELECT * FROM utilisateur WHERE email=?";

		final String REQUEST_READ_CONNECTIONS = "SELECT * FROM utilisateur WHERE utilisateur.email IN ( SELECT utilisateur_connection_email FROM utilisateur_connection WHERE utilisateur_email=?)";
		
		Utilisateur utilisateur = null;
		Utilisateur connection = null;
		ResultSet rsUtilisateur = null;
		ResultSet rsConnections = null;
		
		Connection postgreCon = repositoryTxManager.getConnection();
		try (
			PreparedStatement psUtilisateur = postgreCon.prepareStatement(REQUEST_READ_UTILISATEUR);
			PreparedStatement psConnections = postgreCon.prepareStatement(REQUEST_READ_CONNECTIONS);) {
			
			psUtilisateur.setString(1, email);
			rsUtilisateur = psUtilisateur.executeQuery();

			if (rsUtilisateur.next()) {
				utilisateur = new Utilisateur();
				utilisateur.setEmail(rsUtilisateur.getString("email"));
				utilisateur.setPassword(rsUtilisateur.getString("password"));
				utilisateur.setSolde(rsUtilisateur.getDouble("solde"));
				
				psConnections.setString(1, email);
				rsConnections = psConnections.executeQuery();

				Set<Utilisateur> connections = new HashSet<>();
				
				while(rsConnections.next()) {
					//connections = new HashSet<>();
					connection = new Utilisateur();
					connection.setEmail(rsConnections.getString("email"));
					connection.setPassword(rsConnections.getString("password"));
					connection.setSolde(rsConnections.getDouble("solde"));
					connections.add(connection);
				}
				if (!connections.isEmpty()) {
					utilisateur.setConnection(connections);
				}
			} else {
				logger.error("Error : Utilisateur with email {} not found", email);
			}
			return utilisateur;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (rsUtilisateur != null) {
				try {
					rsUtilisateur.close();
					logger.info("Closing Result Set");
				} catch (SQLException e) {
					logger.error("Error while closing result set", e);
				}
			}
			if (rsConnections != null) {
				try {
					rsConnections.close();
					logger.info("Closing Result Set");
				} catch (SQLException e) {
					logger.error("Error while closing result set", e);
				}
			}
		}
	}
	
	//With transaction
	/**
	 * Delete a user from the repository.
	 * 
	 * @param email The email of the user to delete
	 */
	@Override
	public void delete(String email) {

		final String REQUEST_DELETE = "DELETE FROM utilisateur WHERE email=?";

		Connection postgreCon = null;
		PreparedStatement ps = null;
		try {
			postgreCon = repositoryTxManager.getConnection();
/*
			boolean auto = postgreCon.getAutoCommit();
			postgreCon.setAutoCommit(false);
*/
			ps = postgreCon.prepareStatement(REQUEST_DELETE);

			ps.setString(1, email);

			int updateRowCount = ps.executeUpdate();
/*
			postgreCon.commit();
			postgreCon.setAutoCommit(auto);
*/
			if (updateRowCount != 1) {
				logger.error("Error in User {} deletion", email);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				postgreCon.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} finally {
			if (ps != null) {
				try {
					ps.close();
					logger.info("Closing Prepared Statement");
				} catch (SQLException e) {
					logger.error("Error while closing Prepared Statement", e);
				}
			}
		}

	}

	/**
	 * Add a connection in the repository.
	 * 
	 * @param utilisateur The user for which to add a connection
	 * 
	 * @param connection  The connection to be added to the user
	 */
	@Override
	public void addConnection(Utilisateur utilisateur, Utilisateur connection) {

		final String REQUEST_CREATE = "INSERT INTO utilisateur_connection (utilisateur_email, utilisateur_connection_email) VALUES(?,?)";

		Connection postgreCon = repositoryTxManager.getConnection();
		try (
			PreparedStatement ps = postgreCon.prepareStatement(REQUEST_CREATE)) {

			ps.setString(1, utilisateur.getEmail());
			ps.setString(2, connection.getEmail());
			int updateRowCount = ps.executeUpdate();

			if (updateRowCount != 1) {
				logger.error("Error in Connection insertion");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	// A supprimer
	@Override
	public void updateAll(Utilisateur utilisateur) {

		final String REQUEST_UPDATE_UTILISATEUR = "UPDATE utilisateur SET password=?, solde=? WHERE email=?";

		final String REQUEST_UPDATE_CONNECTIONS = "INSERT INTO utilisateur_connection ( utilisateur_email, utilisateur_connection_email ) VALUES (?,?)";
		
		Connection postgreCon = repositoryTxManager.getConnection();
		try (
			PreparedStatement psUtilisateur = postgreCon.prepareStatement(REQUEST_UPDATE_UTILISATEUR);
			PreparedStatement psConnections = postgreCon.prepareStatement(REQUEST_UPDATE_CONNECTIONS);
						) {
			psUtilisateur.setString(1, utilisateur.getPassword());
			psUtilisateur.setDouble(2, utilisateur.getSolde());
			psUtilisateur.setString(3, utilisateur.getEmail());

			int updateRowCountUtilisateur = psUtilisateur.executeUpdate();

			if (updateRowCountUtilisateur != 1) {
				logger.error("Error in Utilisateur update");
			}

			Set<Utilisateur> currentConnections = read(utilisateur.getEmail()).getConnection();
			Set<Utilisateur> updatedConnections = utilisateur.getConnection();
			//examples.iterator().next()
				
				//if (currentConnections==null && updatedConnections!=null && !updatedConnections.iterator().next().equals(utilisateur.getConnection().iterator().next()   )) {
				//if ( currentConnections==null ) {
				if ( currentConnections==null && updatedConnections!=null ) {
					psConnections.setString(1, utilisateur.getEmail());
					psConnections.setString(2, updatedConnections.iterator().next().getEmail());
					
					int updateRowCountConnections = psConnections.executeUpdate();
					
					if (updateRowCountConnections != 1) {
						logger.error("Error in Utilisateur update");
					}
				} else if( currentConnections!=null && !currentConnections.equals(updatedConnections) ){
					
					for(Utilisateur connection:updatedConnections) {
						if(!currentConnections.contains(connection)) {
							psConnections.setString(1, utilisateur.getEmail());
							psConnections.setString(2, connection.getEmail());
							
							int updateRowCountConnections = psConnections.executeUpdate();
							
							if (updateRowCountConnections != 1) {
								logger.error("Error in Utilisateur update");
							}
					
						}
					}
				}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
