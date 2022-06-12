package model.orm;

import model.data.Prelevement;
import model.orm.exception.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AccessPrelevement {

    public AccessPrelevement() {
    }

    /**
     * Insertion d'un prelevement
     *
     * @param p Le prelevement à insérer
     * @throws DatabaseConnexionException
     * @throws RowNotFoundOrTooManyRowsException
     * @throws DataAccessException
     */
    public void insererPrelevement(Prelevement p) throws DatabaseConnexionException, RowNotFoundOrTooManyRowsException, DataAccessException {
        try {
            Connection con = LogToDatabase.getConnexion();

            String query = "INSERT INTO PrelevementAutomatique VALUES (" + "seq_id_prelevauto.NEXTVAL" + "," + "?"
                    + "," + "?" + "," + "?" + "," + "?" + ")";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setDouble(1, p.montant);
            pst.setInt(2, p.dateRecurrente);
            pst.setString(3, p.beneficiaire);
            pst.setInt(4, p.idNumCompte);

            System.err.println(query);

            int result = pst.executeUpdate();
            pst.close();

            if (result != 1) {
                con.rollback();
                throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.INSERT,
                        "Insert anormale (insert de moins ou plus d'une ligne)", null, result);
            }

            query = "SELECT seq_id_prelevauto.CURRVAL FROM dual";

            System.err.println(query);
            PreparedStatement pst2 = con.prepareStatement(query);

            ResultSet rs = pst2.executeQuery();
            rs.next();
            int numPrelevBase = rs.getInt(1);

            con.commit();
            rs.close();
            pst2.close();

            p.idPrelev = numPrelevBase;


        } catch (SQLException e) {
            throw new DataAccessException(Table.PrelevementAutomatique, Order.INSERT, "Erreur accès", e);
        }
    }

    /**
     * Recherche des prelevements associé à un compte avec le numero de compte
     *
     * @param idNumCompte numero de compte dont on recherche les prelevements
     * @return liste des prelevements associé à un compte
     * @throws DataAccessException
     * @throws DatabaseConnexionException
     */
    public ArrayList<Prelevement> getPrelevements(int idNumCompte) throws DataAccessException, DatabaseConnexionException {
        ArrayList<Prelevement> alResult = new ArrayList<>();

        try {
            Connection con = LogToDatabase.getConnexion();
            String query = "SELECT * FROM PrelevementAutomatique WHERE idNumCompte = ?";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, idNumCompte);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int idPrelev = rs.getInt("idPrelev");
                double montant = rs.getDouble("montant");
                int dateRecurrente = rs.getInt("dateRecurrente");
                String beneficiaire = rs.getString("beneficiaire");
                int idNumCompteTrouve = rs.getInt("idNumCompte");

                alResult.add(new Prelevement(idPrelev, montant, dateRecurrente, beneficiaire, idNumCompteTrouve));
            }

            rs.close();
            pst.close();
            return alResult;
        } catch (SQLException e) {
            throw new DataAccessException(Table.PrelevementAutomatique, Order.SELECT, "Erreur accès", e);
        }
    }

    /**
     * Mise à jour d'un prélèvement
     *
     * prelev.idPrelev est la clé primaire et doit exister, tous les autres champs sont des
     * mise à jours.
     *
     * @param prelev IN Le prélèvement à mettre à jour
     * @throws RowNotFoundOrTooManyRowsException
     * @throws DataAccessException
     * @throws DatabaseConnexionException
     */
    public void updatePrelevement(Prelevement prelev)
            throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
        try {
            Connection con = LogToDatabase.getConnexion();

            String query = "UPDATE PrelevementAutomatique SET montant = ?, dateRecurrente = ?, beneficiaire = ? WHERE idPrelev = ?";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setDouble(1, prelev.montant);
            pst.setInt(2, prelev.dateRecurrente);
            pst.setString(3, prelev.beneficiaire);
            pst.setInt(4, prelev.idPrelev);

            System.err.println(query);

            int result = pst.executeUpdate();
            pst.close();
            if (result != 1) {
                con.rollback();
                throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.UPDATE,
                        "Update anormale (update de moins ou plus d'une ligne)", null, result);
            }
            con.commit();

        } catch (SQLException e) {
            throw new DataAccessException(Table.PrelevementAutomatique, Order.UPDATE, "Erreur accès", e);
        }
    }

    /**
     * Supprime un prelevement.
     *
     * @param prelev IN Le prelevement à supprimer.
     * @throws RowNotFoundOrTooManyRowsException
     * @throws DataAccessException
     * @throws DatabaseConnexionException
     */
    public void deletePrelevement(Prelevement prelev)
            throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
        try  {
            Connection con = LogToDatabase.getConnexion();

            String query = "DELETE FROM PrelevementAutomatique WHERE idPrelev = ?";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, prelev.idPrelev);

            System.err.println(query);

            int result = pst.executeUpdate();
            pst.close();
            if (result != 1) {
                con.rollback();
                throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.DELETE,
                        "Delete anormale (delete de moins ou plus d'une ligne)", null, result);
            }
            con.commit();
        } catch (SQLException e) {
            throw new DataAccessException(Table.PrelevementAutomatique, Order.DELETE, "Erreur accès", e);
        }
    }
}
