package model.data;

import application.control.PrelevementsManagement;

public class Prelevement {
    public int idPrelev;
    public double montant;
    public int dateRecurrente;
    public String beneficiaire;
    public int idNumCompte;

    public Prelevement(int idPrelev, double montant, int dateRecurrente, String beneficiaire, int idNumCompte) {
        this.idPrelev = idPrelev;
        this.montant = montant;
        this.dateRecurrente = dateRecurrente;
        this.beneficiaire = beneficiaire;
        this.idNumCompte = idNumCompte;
    }

    public Prelevement(Prelevement p) {
        this(p.idPrelev, p.montant, p.dateRecurrente, p.beneficiaire, p.idNumCompte);
    }

    @Override
    public String toString() {
        return "Montant=" + montant + ", dateRecurrente=" + dateRecurrente
                + ", beneficiaire=" + beneficiaire;
    }
}
