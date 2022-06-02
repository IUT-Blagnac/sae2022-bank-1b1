package application.control;

import javafx.stage.Stage;
import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import model.data.Client;
import model.data.CompteCourant;

public class CompteEditorPane {

	private Stage primaryStage;
	private CompteEditorPaneController cepc;

	/**
	 * Constructeur de la classe CompteEditorPane permettant de charger la vu d'édition d'un compte
	 * @param _parentStage Stage parent de la vue
	 * @param _dbstate Etat actuel de l'application DailyBank
	 */
	public CompteEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(CompteEditorPaneController.class.getResource("compteeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un compte");
			this.primaryStage.setResizable(false);

			this.cepc = loader.getController();
			this.cepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Active l'affichage de la vu d'édition d'un compte
	 * @param client Le client associé au compte à éditer
	 * @param cpte Le compte à éditer
	 * @param em Le mode d'éditon du compte
	 * @return Le compte édité
	 */
	public CompteCourant doCompteEditorDialog(Client client, CompteCourant cpte, EditionMode em) {
		return this.cepc.displayDialog(client, cpte, em);
	}
}
