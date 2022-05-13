package application;

import application.tools.ConstantesIHM;
import model.data.AgenceBancaire;
import model.data.Employe;

public class DailyBankState {
	private Employe empAct;
	private AgenceBancaire agAct;
	private boolean isChefDAgence;

	/**
	 * Permet de r�cup�rer l'employ� courant
	 * @return Employ� L'employ� courant
	 */
	public Employe getEmpAct() {
		return this.empAct;
	}

	/**
	 * Permet de d�finir l'employ� courant
	 * @param employeActif employ� � d�finir courant
	 */
	public void setEmpAct(Employe employeActif) {
		this.empAct = employeActif;
	}

	/**
	 * Permet de r�cup�rer l'agence bancaire courante
	 * @return AgenceBancaire L'agence bancaire courante
	 */
	public AgenceBancaire getAgAct() {
		return this.agAct;
	}

	/**
	 * Permet de d�finir l'agence bancaire courante
	 * @param agenceActive
	 */
	public void setAgAct(AgenceBancaire agenceActive) {
		this.agAct = agenceActive;
	}

	/**
	 * Permet de savoir si l'employ� courant est les chef d'agence
	 * @return boolean true si l'employ� est chef d'agence, false sinon
	 */
	public boolean isChefDAgence() {
		return this.isChefDAgence;
	}

	/**
	 * Permet de d�finir si l'employ� courant est chef d'agence
	 * @param isChefDAgence bool�en indiquant si l'employ� est chef d'agence
	 */
	public void setChefDAgence(boolean isChefDAgence) {
		this.isChefDAgence = isChefDAgence;
	}

	/**
	 * Permet de d�finir si l'employ� courant est chef d'agence
	 * @param droitsAccess chaine de caract�re contenant le droit d'acc�s pour le chef d'agence
	 */
	public void setChefDAgence(String droitsAccess) {
		this.isChefDAgence = false;

		if (droitsAccess.equals(ConstantesIHM.AGENCE_CHEF)) {
			this.isChefDAgence = true;
		}
	}
}
