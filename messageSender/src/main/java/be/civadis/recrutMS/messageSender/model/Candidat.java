package be.civadis.recrutMS.messageSender.model;

/**
 * Created by phw on 27/10/2017.
 */
public class Candidat {
    private String nom;
    private String prenom;

    public Candidat() {
    }

    public Candidat(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}
