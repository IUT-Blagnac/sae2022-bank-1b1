package application.view;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.PairsOfValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.AccessCompteCourant;
import model.orm.AccessOperation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class SelectionnerEmplacementController implements Initializable {

		private DailyBankState dbs;
		private Client client;
		private CompteCourant compte;
		private String chemin;
		

		// Fenêtre physique
		private Stage primaryStage;
		
		@FXML
		Button boutonSelectionner;
		
		@FXML
		Button boutonGenerer;
		
		@FXML
		Button boutonAnnuler;
		
		@FXML
		TextField zoneNomFichier;
		
		@FXML
		Label texteErreur;
		
		@FXML
		Label texteCheminFichier;

		@Override
		public void initialize(URL location, ResourceBundle resources) {
		}
		
		public void initContext(Stage _primaryStage, DailyBankState _dbstate, Client client, CompteCourant compte) {
			this.primaryStage = _primaryStage;
			this.dbs = _dbstate;
			this.client = client;
			this.compte = compte;
			this.configure();
		}

		private void configure() {
			this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		}
		
		// Gestion du stage
		private Object closeWindow(WindowEvent e) {
			this.doCancel();
			e.consume();
			return null;
		}
		
		@FXML
		private void choisirCheminFichier() {
			
			DirectoryChooser directoryChooser = new DirectoryChooser();
	        directoryChooser.setInitialDirectory(new File("src"));
			
			File selectedDirectory = directoryChooser.showDialog(primaryStage);
			
			if (selectedDirectory != null) {
				this.chemin = selectedDirectory.getAbsolutePath();
			}
			
			texteCheminFichier.setText("Emplacement du fichier "+this.chemin);
			
		}
		
		@FXML
		private void doGenerer() {
			boolean isValide = true;
			
			if (this.chemin == null) {
				this.texteErreur.setText("Veuillez entrer un chemin où enregistrer votre fichier");
				isValide = false;
			}
			if (this.zoneNomFichier.getText().equals("")) {
				this.texteErreur.setText("Veuillez donner un nom à votre fichier");
				isValide = false;
			}
			if (isValide) {
				this.texteErreur.setText("Relevé de compte à coder !");
				System.out.println("Chemin : "+this.chemin);
				System.out.println("Nom du fichier : "+this.zoneNomFichier.getText());
				System.out.println("Client : "+this.client.toString());
				System.out.println("Compte : "+this.compte.toString());
				try {
					System.out.println("Opérations : "+getOperationsDuCompte().toString());
				} catch (Exception E) {
					System.out.println(E);
				}
			}
		}
		
		/*
		 * Méthode locale, utilisée pour récupérer les opérations du compte afin de générer le relevé pdf
		 * Aucun paramètre n'est nécessaire car les arguments sont des attributs locaux de la classe
		 */
		private ArrayList<Operation> getOperationsDuCompte() throws Exception {
			
			ArrayList<Operation> listeOP = new ArrayList<>();

			try {
				// Relecture BD du solde du compte
				AccessCompteCourant acc = new AccessCompteCourant();
				this.compte = acc.getCompteCourant(this.compte.idNumCompte);

				// lecture BD de la liste des opérations du compte de l'utilisateur
				AccessOperation ao = new AccessOperation();
				listeOP = ao.getOperations(this.compte.idNumCompte);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				listeOP = new ArrayList<>();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				listeOP = new ArrayList<>();
			}
			System.out.println(this.compte.solde);
			return listeOP;
		}
		
		@FXML
		private void doCancel() {
			this.primaryStage.close();
		}
		
		public void displayDialog() {
			this.primaryStage.showAndWait();
		}
}
