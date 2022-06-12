package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.PrelevementsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;
import model.orm.AccessCompteCourant;
import model.orm.AccessPrelevement;
import model.orm.exception.ApplicationException;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

import java.util.ArrayList;

public class PrelevementsManagement {

    private Stage primaryStage;
    private DailyBankState dbs;
    private PrelevementsManagementController pmc;
    private Client clientDuCompte;
    private CompteCourant compteConcerne;

    /**
     * Constructeur de la classe PrelevementsManagement permettant de charger la vu de gestion des prelevements
     * @param _primaryStage Stage parent de la vue
     * @param _dbs Etat actuel de l'application DailyBank
     * @param _clientDuCompte Client dont on veut afficher les prelevements
     * @param _compteConcerne Compte dont on veut afficher les prelevements
     */
    public PrelevementsManagement(Stage _primaryStage, DailyBankState _dbs, Client _clientDuCompte, CompteCourant _compteConcerne) {

        this.clientDuCompte = _clientDuCompte;
        this.compteConcerne = _compteConcerne;
        this.dbs = _dbs;
        try {
            FXMLLoader loader = new FXMLLoader(
                    PrelevementsManagementController.class.getResource("prelevementsManagement.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, 900 + 20, 350 + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_primaryStage);
            StageManagement.manageCenteringStage(_primaryStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion des prélèvements");
            this.primaryStage.setResizable(false);

            this.pmc = loader.getController();
            this.pmc.initContext(this.primaryStage, this, _dbs, _clientDuCompte, this.compteConcerne);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Active la vu de gestion des prelevements
     */
    public void doPrelevementsManagementDialog() { this.pmc.displayDialog(); }

    /**
     * Active la vu d'ajout d'un prelevement
     * @return Le nouveau prelevement créé
     */
    public Prelevement creerPrelev() {
        Prelevement prelev;
        PrelevementEditorPane pep = new PrelevementEditorPane(this.primaryStage, this.dbs);
        prelev = pep.doPrelevementEditorDialog(this.compteConcerne, null, EditionMode.CREATION);
        if (prelev != null) {
            try {
                AccessPrelevement ap = new AccessPrelevement();

                ap.insererPrelevement(prelev);
            } catch (RowNotFoundOrTooManyRowsException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
                prelev = null;
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
                prelev = null;
            } catch (DataAccessException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
                prelev = null;
            }
        }

        return prelev;
    }

    /**
     * Active la vu de modification d'un prelevement
     * @param p Le prelevement à modifier
     * @return Le prelevement modifie
     */
    public Prelevement modifierPrelev(Prelevement p) {
        PrelevementEditorPane pep = new PrelevementEditorPane(this.primaryStage, this.dbs);
        Prelevement result = pep.doPrelevementEditorDialog(this.compteConcerne, p, EditionMode.MODIFICATION);
        if (result != null) {
            try {
                AccessPrelevement ap = new AccessPrelevement();

                ap.updatePrelevement(result);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
                ed.doExceptionDialog();
                result = null;
                this.primaryStage.close();
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
                ed.doExceptionDialog();
                result = null;
            }
        }

        return result;
    }

    /**
     * Active la vu de suppression d'un prelevement
     * @param prelev Le prelevement à supprimer
     */
    public void supprimerPrelev(Prelevement prelev) {
        try {
            AccessPrelevement ap = new AccessPrelevement();

            ap.deletePrelevement(prelev);
        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
            ed.doExceptionDialog();
            this.primaryStage.close();
        } catch (ApplicationException ae) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
            ed.doExceptionDialog();
        }
    }

    /**
     * Recupere les prelevements d'un compte
     * @return La liste des prelevements du compte avec le compte associe
     */
    public PairsOfValue<CompteCourant, ArrayList<Prelevement>> prelevementsEtSoldeDunCompte() {
        ArrayList<Prelevement> listeP = new ArrayList<>();

        try {
            AccessCompteCourant acc = new AccessCompteCourant();
            this.compteConcerne = acc.getCompteCourant(this.compteConcerne.idNumCompte);

            AccessPrelevement ap = new AccessPrelevement();
            listeP = ap.getPrelevements(this.compteConcerne.idNumCompte);
        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
            ed.doExceptionDialog();
            this.primaryStage.close();
            listeP = new ArrayList<>();
        } catch (ApplicationException ae) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
            ed.doExceptionDialog();
            listeP = new ArrayList<>();
        }

        return new PairsOfValue<>(this.compteConcerne, listeP);
    }


}
