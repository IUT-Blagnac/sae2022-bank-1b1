package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.StageManagement;
import application.view.OperationsManagementController;
import application.view.SelectionnerEmplacementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;

public class SelectionnerEmplacement {
	
	private Stage primaryStage;
	private Client client;
	private CompteCourant compte;
	private DailyBankState dbs;
	private SelectionnerEmplacementController sec;

	public SelectionnerEmplacement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant compte) {

		this.client = client;
		this.compte = compte;
		this.dbs = _dbstate;
		try {
			
			FXMLLoader loader = new FXMLLoader(
					OperationsManagementController.class.getResource("selectionnerEmplacement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Générer un relevé de compte pdf");
			this.primaryStage.setResizable(false);

			this.sec = loader.getController();
			this.sec.initContext(this.primaryStage, _dbstate,  this.client, this.compte);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doSelectionnerEmplacement() {
		this.sec.displayDialog();
	}
}