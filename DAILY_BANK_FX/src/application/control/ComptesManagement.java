package application.control;

import java.util.ArrayList;

import javafx.stage.Stage;
import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.AccessCompteCourant;
import model.orm.exception.*;

public class ComptesManagement {

	private Stage primaryStage;
	private ComptesManagementController cmc;
	private DailyBankState dbs;
	private Client clientDesComptes;

	/**
	 * Constructeur de la classe ComptesManagement permettant de charger la vu de gestion des comptes
	 * @param _parentStage Stage parent de la vue
	 * @param _dbstate Etat actuel de l'application DailyBank
	 * @param client Client dont on veut afficher les comptes
	 */
	public ComptesManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

		this.clientDesComptes = client;
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("comptesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des comptes");
			this.primaryStage.setResizable(false);

			this.cmc = loader.getController();
			this.cmc.initContext(this.primaryStage, this, _dbstate, client);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Active la vu des gestion des comptes d'un client
	 */
	public void doComptesManagementDialog() {
		this.cmc.displayDialog();
	}

	/**
	 * Active la vu de gestion des opération d'un compte
	 * @param cpt Compte dont on veut afficher les opérations
	 */
	public void gererOperations(CompteCourant cpt) {
		OperationsManagement om = new OperationsManagement(this.primaryStage, this.dbs, this.clientDesComptes, cpt);
		om.doOperationsManagementDialog();
	}

	/**
	 * Active la vu d'ajout d'un nouveau compte
	 * @return Le nouveau compte créé
	 */
	public CompteCourant creerCompte() {
		CompteCourant compte;
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dbs);
		compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);
		if (compte != null) {
			try {
				AccessCompteCourant acc = new AccessCompteCourant();

				acc.insertCompteCourant(compte);
			} catch (ManagementRuleViolation me) {
				AlertUtilities.showAlert(this.primaryStage, "Erreur", "Montant initial trop faible",
						"Le montant initial doit être supérieur à 50", AlertType.ERROR);
				compte = null;
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				compte = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				compte = null;
			}
		}
		return compte;
	}

	/**
	 * Récupère les comptes d'un client
	 * @return La liste des comptes d'un client
	 */
	public ArrayList<CompteCourant> getComptesDunClient() {
		ArrayList<CompteCourant> listeCpt = new ArrayList<>();

		try {
			AccessCompteCourant acc = new AccessCompteCourant();
			listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCpt = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
		}
		return listeCpt;
	}
}
