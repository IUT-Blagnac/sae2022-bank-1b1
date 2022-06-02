package application.control;

import javafx.stage.Stage;
import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.LoginDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import model.data.Employe;
import model.orm.AccessEmploye;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class LoginDialog {

	private Stage primaryStage;
	private DailyBankState dbs;
	private LoginDialogController ldc;

	/*
	 * Charge la fenêtre qui permet de se connecter à l'application
	 * @param _parentStage Stage le stage parent
	 * @param _dbstate DailyBankState l'état de la base de donnée
	 */
	public LoginDialog(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(LoginDialogController.class.getResource("logindialog.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Identification");
			this.primaryStage.setResizable(false);

			this.ldc = loader.getController();
			this.ldc.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doLoginDialog() {
		this.ldc.displayDialog();
	}

	/**
	 * Recherche un employé avec son login et son mot de passe
	 * 
	 * @param login String le nom d'utilisateur de l'employé à chercher
	 * @param password String le mot de passe de l'employé à chercher
	 * @return Employe l'employé trouvé, null sinon
	 */
	public Employe chercherParLogin(String login, String password) {
		Employe employe = null;
		try {
			AccessEmploye ae = new AccessEmploye();

			employe = ae.getEmploye(login, password);

			if (employe != null) {
				this.dbs.setEmpAct(employe);
			}
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.dbs.setEmpAct(null);
			this.primaryStage.close();
			employe = null;
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			this.dbs.setEmpAct(null);
			this.primaryStage.close();
			employe = null;
		}
		return employe;
	}
}
