package application.view;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;

import application.DailyBankState;
import application.control.ExceptionDialog;
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
		
		//Bouton qui génère le fichier pdf
		@FXML
		private void doGenerer() {
			boolean isValide = true;
			String erreur = "";
			
			if (this.chemin == null) {
				erreur = erreur + "Veuillez entrer un chemin où enregistrer votre fichier" + "\n";
				isValide = false;
			}
			if (this.zoneNomFichier.getText().equals("")) {
				erreur = erreur + "Veuillez donner un nom à votre fichier" + "\n";
				isValide = false;
			}
			
			//Si le chemin du fichier et le nom du fichier sont valides
			if (isValide) {
				String PATH = this.chemin + "/" + this.zoneNomFichier.getText() + ".pdf" ;
				try {
					genererPDF(PATH);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			//Sinon on indique à l'utilisateur le ou les erreurs
			} else {
				this.texteErreur.setText(erreur);
			}
		}

		private void genererPDF(String PATH) throws Exception {

			try {
				
				//Polices d'écriture
				Font policeTitre = new Font(Font.FontFamily.TIMES_ROMAN, 18,
			            Font.BOLD);
				Font policeTitre2 = new Font(Font.FontFamily.TIMES_ROMAN, 14,
			            Font.BOLD);
				
				//Initialisation du document
				Document document = new Document();
	            PdfWriter.getInstance(document, new FileOutputStream(PATH));
	            document.open();
	            
	            Paragraph preface = new Paragraph();
	            
	            //Titre du document PDF
	            preface.add(new Paragraph("Relevé de compte de "+client.prenom+" "+client.nom, policeTitre));
	            preface.add(new Paragraph("Compte bancaire numéro "+compte.idNumCompte, policeTitre));
	            
	            //Ajout de la date
	            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz");
	            Date date = new Date();
	            preface.add(new Paragraph(sdf.format(date),policeTitre2));
	            
	            
	            document.add(preface);
	            
	            document.add(new Paragraph(" "));
	            
	            //Ajout du solde restant
	            preface = new Paragraph();
	            preface.add(new Paragraph("Solde restant actuel : "+compte.solde+" €"));
	            document.add(preface);
	            
	            document.add(new Paragraph(" "));
	            
	            
	            //Ajout de la table des opérations du compte
	            PdfPTable table = new PdfPTable(3);

	            PdfPCell c1 = new PdfPCell(new Phrase("Date"));
	            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(c1);

	            c1 = new PdfPCell(new Phrase("Type d'opération"));
	            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(c1);

	            c1 = new PdfPCell(new Phrase("Montant"));
	            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(c1);
	            table.setHeaderRows(1);

	            ArrayList<Operation> operations = getOperationsDuCompte();
	            int len = operations.size();

	            for (int i=0 ; i<len ; i++) {
	            	table.addCell(""+operations.get(i).dateOp);
	            	table.addCell(operations.get(i).idTypeOp);
	            	table.addCell(""+operations.get(i).montant+" €");
	            }

	            //Ajout de la table des opérations
	            document.add(table);

	            //Finalement on ferme le document pdf car tout a été écrit
	            document.close();

	            //Une fois tout effectué, on indique à l'utilisateur que le pdf a bien été généré sur la fenêtre
				this.texteErreur.setText("Relevé de compte enregistré !");

				//Si il y a eu une erreur lors de la création du pdf on l'indique à l'utilisateur
			} catch (Exception E) {
				this.texteErreur.setText("Erreur lors de l'enregistrement du relevé...");
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
