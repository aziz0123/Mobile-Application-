package tn.azizexample.healthlink.model;

public class rappel {
    private int id; // identifiant unique pour chaque rappel
    private String nomMedicament;
    private String heureRappel;

    // Constructeur
    public rappel(int id, String nomMedicament, String heureRappel) {
        this.id = id;
        this.nomMedicament = nomMedicament;
        this.heureRappel = heureRappel;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomMedicament() { return nomMedicament; }
    public void setNomMedicament(String nomMedicament) { this.nomMedicament = nomMedicament; }

    public String getHeureRappel() { return heureRappel; }
    public void setHeureRappel(String heureRappel) { this.heureRappel = heureRappel; }
}
