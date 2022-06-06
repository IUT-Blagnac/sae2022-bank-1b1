package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.StageManagement;
import application.view.OperationEditorPaneController;
import application.view.VirerEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Operation;
import model.data.Virement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

public class VirementEditorPane {

	private Stage primaryStage;
	private VirerEditorPaneController vepc;

	/**
	 * Constructeur de la classe OperationEditorPane permettant de charger la vu d'édition des opérations d'un compte
	* @param _parentStage Stage parent de la vue
	 * @param _dbstate Etat actuel de l'application DailyBank
	 */
	public VirementEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(
					VirerEditorPaneController.class.getResource("virereditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 500 + 20, 250 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Enregistrement d'une opération");
			this.primaryStage.setResizable(false);

			this.vepc = loader.getController();
			this.vepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Active l'affichage de la vu d'édition des opération d'un compte
	 * @param cpte Compte dont on veut modifier les opérations
	 * @return L'opération modifié
	 */
	public Virement doVirementEditorDialog(CompteCourant cpte) throws DatabaseConnexionException, DataAccessException {
		return this.vepc.displayDialog(cpte);
	}
}
