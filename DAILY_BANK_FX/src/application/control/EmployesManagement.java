package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
	public Employe modifierEmploye(Employe e) {
		EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dbs);
		Employe result = eep.doEmployeEditorDialog(e, EditionMode.MODIFICATION);
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
	public Employe nouveauEmploye() {
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
	
	// ANTON - UN EMPLOYE CA A PAS DE COMPTES BANCAIRES DU COUP J'IMAGINE CE TRUC JE LE SUPPRIME !!!!
	
	/**
	 * Active l'affichage de la vue de gestion des comptes employes
	 * @param e Employe dont on veut afficher les comptes
	 */
	public void gererComptesEmploye(Employe e) {
		ComptesManagement cm = new ComptesManagement(this.primaryStage, this.dbs, e);
		cm.doComptesManagementDialog();
	}
	
	// ANTON - ET CA AUSSI ???
	
	/**
	 * Récupère la liste des compte d'un employe
	 * @param _numCompte Numéro du compte à chercher
	 * @param _debutNom Début du nom du employe associé au compte à chercher
	 * @param _debutPrenom Début du prénom du employe associé au compte à chercher
	 * @return Liste la lite des comptes trouvés
	 */
	public ArrayList<Employe> getlisteComptes(int _numCompte, String _debutNom, String _debutPrenom) {
		ArrayList<Employe> listeEmp = new ArrayList<>();
		try {
			// Recherche des employes en BD. cf. AccessEmploye > getEmployes(.)
			// numCompte != -1 => recherche sur numCompte
			// numCompte != -1 et debutNom non vide => recherche nom/prenom
			// numCompte != -1 et debutNom vide => recherche tous les employes

			AccessEmploye ae = new AccessEmploye();
			listeEmp = ae.getEmployes(this.dbs.getEmpAct().idAg, _numCompte, _debutNom, _debutPrenom);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeEmp = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeEmp = new ArrayList<>();
		}
		return listeEmp;
	}
}
