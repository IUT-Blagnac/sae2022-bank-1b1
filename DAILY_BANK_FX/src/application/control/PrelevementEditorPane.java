package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.OperationEditorPaneController;
import application.view.PrelevementEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Operation;
import model.data.Prelevement;

public class PrelevementEditorPane {

	private Stage primaryStage;
	private PrelevementEditorPaneController pepc;

	/**
	 * Constructeur de la classe PrelevementEditorPane permettant de charger la vu d'édition des prelevements d'un compte
	* @param _parentStage Stage parent de la vue
	 * @param _dbstate Etat actuel de l'application DailyBank
	 */
	public PrelevementEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(
					OperationEditorPaneController.class.getResource("prelevementeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Enregistrement d'une opération");
			this.primaryStage.setResizable(false);

			this.pepc = loader.getController();
			this.pepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Active l'affichage de la vu d'édition des prelevments d'un compte
	 * @param cpte Compte dont on veut modifier les prelevements
	 * @return Le prelevement modifié
	 */
	public Prelevement doPrelevementEditorDialog(CompteCourant cpte, Prelevement pp, EditionMode em) {
		return this.pepc.displayDialog(cpte, pp, em);
	}
}
