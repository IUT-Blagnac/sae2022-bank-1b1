package application.view;

import application.DailyBankState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Virement;
import model.orm.AccessCompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class VirerEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private CompteCourant compteEdite;
	private Virement virementResultat;

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
	 * Affiche la boite de dialogue de mofication des opérations
	 * @param cpte Compte dont on veut modifier les opérations
	 * @return L'opération modifié
	 */
	public Virement displayDialog(CompteCourant cpte) throws DatabaseConnexionException, DataAccessException {
		this.compteEdite = cpte;

		String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
		this.lblMessage.setText(info);

		this.btnOk.setText("Effectuer Virement");
		this.btnCancel.setText("Annuler Virement");

		ObservableList<CompteCourant> list = FXCollections.observableArrayList();
		AccessCompteCourant acc = new AccessCompteCourant();
		ArrayList<CompteCourant> listeComtes = acc.getCompteCourants(this.compteEdite.idNumCli);

		for (CompteCourant c: listeComtes) {
			if (c.idNumCompte != this.compteEdite.idNumCompte) {
				list.add(c);
			}
		}

		this.cbTypeOpe.setItems(list);
		this.cbTypeOpe.getSelectionModel().select(0);

		this.virementResultat = null;
		this.cbTypeOpe.requestFocus();

		this.primaryStage.showAndWait();
		return this.virementResultat;
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
	private ComboBox<CompteCourant> cbTypeOpe;
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
		this.virementResultat = null;
		this.primaryStage.close();
	}

	@FXML
	private void doAjouter() {
		double montant;

		this.txtMontant.getStyleClass().remove("borderred");
		this.lblMontant.getStyleClass().remove("borderred");
		this.lblMessage.getStyleClass().remove("borderred");
		
		String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
		this.lblMessage.setText(info);

		try {
			montant = Double.parseDouble(this.txtMontant.getText().trim());
			if (montant <= 0)
				throw new NumberFormatException();
		} catch (NumberFormatException nfe) {
			this.txtMontant.getStyleClass().add("borderred");
			this.lblMontant.getStyleClass().add("borderred");
			this.txtMontant.requestFocus();
			return;
		}
		if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise) {
			info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);
			this.txtMontant.getStyleClass().add("borderred");
			this.lblMontant.getStyleClass().add("borderred");
			this.lblMessage.getStyleClass().add("borderred");
			this.txtMontant.requestFocus();
			return;
		}

		CompteCourant cpteCre = this.cbTypeOpe.getValue();
		this.virementResultat = new Virement(this.compteEdite.idNumCompte, cpteCre.idNumCompte, montant);
		System.out.println(cpteCre.idNumCompte);
		this.primaryStage.close();
	}
}
