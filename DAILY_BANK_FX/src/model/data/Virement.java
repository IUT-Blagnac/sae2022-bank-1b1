package model.data;

public class Virement {
    public int idNumCompteDeb;
    public int idNumCompteCred;
    public double montant;
    public Virement(int idNumCompte, int idNumCompte1, double montant) {
        this.idNumCompteDeb = idNumCompte;
        this.idNumCompteCred = idNumCompte1;
        this.montant = montant;
    }
}
