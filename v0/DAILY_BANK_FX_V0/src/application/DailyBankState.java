package application;

import application.tools.ConstantesIHM;
import model.data.AgenceBancaire;
import model.data.Employe;

public class DailyBankState {
	private Employe empAct;
	private AgenceBancaire agAct;
	private boolean isChefDAgence;

	/**
	 * Permet de récupérer l'employé courant
	 * @return Employé L'employé courant
	 */
	public Employe getEmpAct() {
		return this.empAct;
	}

	/**
	 * Permet de définir l'employé courant
	 * @param employeActif employé à définir courant
	 */
	public void setEmpAct(Employe employeActif) {
		this.empAct = employeActif;
	}

	/**
	 * Permet de récupérer l'agence bancaire courante
	 * @return AgenceBancaire L'agence bancaire courante
	 */
	public AgenceBancaire getAgAct() {
		return this.agAct;
	}

	/**
	 * Permet de définir l'agence bancaire courante
	 * @param agenceActive
	 */
	public void setAgAct(AgenceBancaire agenceActive) {
		this.agAct = agenceActive;
	}

	/**
	 * Permet de savoir si l'employé courant est les chef d'agence
	 * @return boolean true si l'employé est chef d'agence, false sinon
	 */
	public boolean isChefDAgence() {
		return this.isChefDAgence;
	}

	/**
	 * Permet de définir si l'employé courant est chef d'agence
	 * @param isChefDAgence booléen indiquant si l'employé est chef d'agence
	 */
	public void setChefDAgence(boolean isChefDAgence) {
		this.isChefDAgence = isChefDAgence;
	}

	/**
	 * Permet de définir si l'employé courant est chef d'agence
	 * @param droitsAccess chaine de caractère contenant le droit d'accés pour le chef d'agence
	 */
	public void setChefDAgence(String droitsAccess) {
		this.isChefDAgence = false;

		if (droitsAccess.equals(ConstantesIHM.AGENCE_CHEF)) {
			this.isChefDAgence = true;
		}
	}
}
