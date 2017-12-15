package be.civadis.recrutMS.messageListenerPosteCandidat.model;

/**
 * Created by phw on 26/10/2017.
 */
public class Poste {
    private String nr;
    private String titre;

    public Poste() {
    }

    public Poste(String nr, String titre) {
        this.nr = nr;
        this.titre = titre;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }
}
