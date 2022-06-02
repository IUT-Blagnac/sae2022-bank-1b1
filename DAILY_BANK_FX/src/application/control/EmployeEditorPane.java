package application.control;

import javafx.stage.Stage;
import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployeEditorPaneController;
import application.view.EmployesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import model.data.Employe;

public class EmployeEditorPane {

	private Stage primaryStage;
	private EmployeEditorPaneController eepc;

	/**
	 * Constructeur de la classe EmployeEditorPane permettant de charger la vue d'édition d'un employe
	 * @param _parentStage Stage parent de la vue
	 * @param _dbstate Etat actuel de l'application DailyBank
	 */
	public EmployeEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(EmployesManagementController.class.getResource("employeeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un employe");
			this.primaryStage.setResizable(false);

			this.eepc = loader.getController();
			this.eepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Active l'affichage de la vue d'édition d'un employe
	 * @param employe L'employe à modifier
	 * @param em Le mode d'édition
	 * @return L'employe modifié
	 */
	public Employe doEmployeEditorDialog(Employe employe, EditionMode em) {
		return this.eepc.displayDialog(employe, em);
	}
}
