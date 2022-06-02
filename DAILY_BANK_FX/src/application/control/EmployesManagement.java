package application.control;

import java.util.ArrayList;

import javafx.stage.Stage;
import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import model.data.Employe;
import model.orm.AccessEmploye;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployesManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private EmployesManagementController emc;

	/**
	 * Constructeur de la classe EmployesManagement permettant de charger la vue de gestion des employes
	 * @param _parentStage Stage parent de la vue
	 * @param _dbstate Etat actuel de l'application DailyBank
	 */
	public EmployesManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmployesManagementController.class.getResource("employesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage); 
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des employes");
			this.primaryStage.setResizable(false);

			this.emc = loader.getController();
			this.emc.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Active l'affichage de la vue de gestion des employes
	 */
	public void doEmployeManagementDialog() {
		this.emc.displayDialog();
	}

	/**
	 * Active l'affichage de la vue de modification d'un employe
	 * @param e Le employe à modifier
	 * @return Le employe modifié
	 */
	public Employe modifierEmploye(Employe emp) {
		EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dbs);
		Employe result = eep.doEmployeEditorDialog(emp, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AccessEmploye ae = new AccessEmploye();
				ae.updateEmploye(result);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				result = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				result = null;
			}
		}
		return result;
	}

	/**
	 * Active l'affichage de la vue d'ajout d'un employe
	 * @return Le nouvel employe créé
	 */
	public Employe nouvelEmploye() {
		Employe employe;
		EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dbs);
		employe = eep.doEmployeEditorDialog(null, EditionMode.CREATION);
		if (employe != null) {
			try {
				AccessEmploye ae = new AccessEmploye();

				ae.insertEmploye(employe);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				employe = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				employe = null;
			}
		}
		return employe;
	}
	
}