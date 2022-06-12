package application.view;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class PrelevementEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private EditionMode em;
	private CompteCourant cc;
	private Prelevement prelevementEdite;
	private Prelevement prelevementResult;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}



	/**
	 * Affiche la boite de dialogue d'édition d'un prelevement
	 * @param cpte Compte à modifier
	 * @param prlv Prelevement à modifier
	 * @param mode Le mode d'édition sélectionné
	 * @return Le prelevement modifié
	 */
	public Prelevement displayDialog(CompteCourant cpte, Prelevement prlv, EditionMode mode) {
		this.cc = cpte;
		this.em = mode;
		if (prlv == null) {
			this.prelevementEdite = new Prelevement(0, 200, 1, "", this.cc.idNumCompte);

		} else {
			this.prelevementEdite = new Prelevement(prlv);
		}


		this.prelevementResult = null;
		this.txtNumCompte.setDisable(true);

		this.txtBeneficiaire.setDisable(false);
		this.txtMontant.setDisable(false);
		this.lblMessage.setText("Informations sur le nouveau prélèvement");
		this.lblMontant.setText("Montant");
		this.btnOk.setText("Ajouter");
		this.btnCancel.setText("Annuler");

		// initialisation du contenu des champs
		this.txtNumCompte.setText("" + this.prelevementEdite.idNumCompte);
		this.txtBeneficiaire.setText("" + this.prelevementEdite.beneficiaire);
		this.txtDate.setText("" + this.prelevementEdite.dateRecurrente);
		this.txtMontant.setText(String.format(Locale.ENGLISH, "%10.02f", this.prelevementEdite.montant));

		this.prelevementResult = null;

		this.primaryStage.showAndWait();
		return this.prelevementResult;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblMessage;
	@FXML
	private Label lblMontant;
	@FXML
	private Label lblBeneficiaire;
	@FXML
	private Label lblDate;
	@FXML
	private TextField txtNumCompte;
	@FXML
	private TextField txtBeneficiaire;
	@FXML
	private TextField txtDate;
	@FXML
	private TextField txtMontant;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.prelevementResult = null;
		this.primaryStage.close();
	}

	@FXML
	private void doAjouter() {
		if (this.isSaisieValide()) {
			this.prelevementResult = this.prelevementEdite;
			this.primaryStage.close();
		}
	}

	private boolean isSaisieValide() {
		this.prelevementEdite.montant = Double.parseDouble(this.txtMontant.getText().trim());
		this.prelevementEdite.beneficiaire = this.txtBeneficiaire.getText().trim();
		this.prelevementEdite.dateRecurrente = Integer.parseInt(this.txtDate.getText().trim());
		this.prelevementEdite.idNumCompte = Integer.parseInt(this.txtNumCompte.getText().trim());

		this.txtBeneficiaire.getStyleClass().remove("borderred");
		this.txtDate.getStyleClass().remove("borderred");
		this.txtMontant.getStyleClass().remove("borderred");
		this.lblBeneficiaire.getStyleClass().remove("borderred");
		this.lblDate.getStyleClass().remove("borderred");
		this.lblMontant.getStyleClass().remove("borderred");


		if (this.prelevementEdite.beneficiaire.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur", "Le nom du bénéficiaire est obligatoire", null, AlertType.ERROR);
			this.txtBeneficiaire.getStyleClass().add("borderred");
			this.lblBeneficiaire.getStyleClass().add("borderred");
			this.txtBeneficiaire.requestFocus();

			return false;
		}

		if (this.prelevementEdite.dateRecurrente <= 0 || this.prelevementEdite.dateRecurrente > 31) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur", "La date doit être comprise entre 0 et 31", null, AlertType.ERROR);
			this.txtDate.getStyleClass().add("borderred");
			this.lblDate.getStyleClass().add("borderred");
			this.txtDate.requestFocus();

			return false;
		}

		if (this.prelevementEdite.montant <= 0) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur", "Le montant doit être positif", null, AlertType.ERROR);
			this.txtMontant.getStyleClass().add("borderred");
			this.lblMontant.getStyleClass().add("borderred");
			this.txtMontant.requestFocus();

			return false;
		}

		return true;
	}
}
