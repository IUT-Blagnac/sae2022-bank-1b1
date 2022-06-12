package application.view;

import application.DailyBankState;
import application.control.OperationsManagement;
import application.control.PrelevementsManagement;
import application.tools.AlertUtilities;
import application.tools.NoSelectionModel;
import application.tools.PairsOfValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.*;
import model.orm.AccessCompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PrelevementsManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private PrelevementsManagement pm;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDuCompte;
	private CompteCourant compteConcerne;
	private ObservableList<Prelevement> olPrelevements;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, PrelevementsManagement _pm, DailyBankState _dbstate, Client client, CompteCourant compte) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.pm = _pm;
		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olPrelevements = FXCollections.observableArrayList();
		this.lvPrelevements.setItems(this.olPrelevements);
		this.lvPrelevements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvPrelevements.getFocusModel().focus(-1);
		this.lvPrelevements.getSelectionModel().selectedIndexProperty().addListener(e -> this.validateComponentState());
		this.updateInfoCompteClient();
		this.validateComponentState();
	}

	/**
	 * Affiche la vu des gestion des prelevements d'un compte
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblInfosClient;
	@FXML
	private Label lblInfosCompte;
	@FXML
	private ListView<Prelevement> lvPrelevements;
	@FXML
	private Button btnCreerPrelev;
	@FXML
	private Button btnModifierPrelev;

	@FXML
	private Button btnSupprimerPrelev;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doCreerPrelev() {

		Prelevement p = this.pm.creerPrelev();
		if (p != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	@FXML
	private void doModifierPrelev() {
		int selectedIndex = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			Prelevement pMod = this.olPrelevements.get(selectedIndex);
			Prelevement p = this.pm.modifierPrelev(pMod);

			if (p != null) {
				this.updateInfoCompteClient();
				this.validateComponentState();
			}
		}

	}

	@FXML
	private void doSupprimerPrelev() {
		int selectedIndex = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			Prelevement pSup = this.olPrelevements.get(selectedIndex);
			boolean confirm = AlertUtilities.confirmYesCancel(primaryStage, "Confirmation de suppression", "Voulez-vous vraiment supprimer ce prélèvement ?", pSup.toString(), Alert.AlertType.CONFIRMATION);

			if (confirm) {
				this.pm.supprimerPrelev(pSup);
				this.olPrelevements.remove(selectedIndex);
			}
		}
	}

	private void validateComponentState() {
		int selectedIndex = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			this.btnModifierPrelev.setDisable(false);
			this.btnSupprimerPrelev.setDisable(false);
		} else {
			this.btnModifierPrelev.setDisable(true);
			this.btnSupprimerPrelev.setDisable(true);
		}
		this.btnCreerPrelev.setDisable(false);
	}

	private void updateInfoCompteClient() {

		PairsOfValue<CompteCourant, ArrayList<Prelevement>> prelevEtCompte;

		prelevEtCompte = this.pm.prelevementsEtSoldeDunCompte();

		ArrayList<Prelevement> listeP;
		this.compteConcerne = prelevEtCompte.getLeft();
		listeP = prelevEtCompte.getRight();

		String info;
		info = this.clientDuCompte.nom + "  " + this.clientDuCompte.prenom + "  (id : " + this.clientDuCompte.idNumCli
				+ ")";
		this.lblInfosClient.setText(info);

		info = "Cpt. : " + this.compteConcerne.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteConcerne.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteConcerne.debitAutorise);
		this.lblInfosCompte.setText(info);

		this.olPrelevements.clear();
		for (Prelevement p : listeP) {
			this.olPrelevements.add(p);
		}

		this.validateComponentState();
	}
}
