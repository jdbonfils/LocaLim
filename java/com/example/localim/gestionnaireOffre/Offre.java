package com.example.localim.gestionnaireOffre;

import android.graphics.Bitmap;
import android.location.Address;
import android.net.Uri;

import java.io.Serializable;
//On implements serializable pour pouvoir enregistrer l'objet en BD et pour pouvoir passer l'objet d'une activité a une autre
public class Offre implements Serializable {
    private String mNom;
    private String mDescription;
    private String mAdresse ;
    private String ressourceId ;

    public Offre() {}

    //Seul l'Objet OffreManager crée des offres
    protected Offre(String nom , String description, String adresse)
    {
        mNom = nom ;
        mDescription = description ;
        mAdresse = adresse ;
    }
    protected Offre(String nom , String description,String adresse ,String key )
    {
        mNom = nom ;
        mDescription = description ;
        ressourceId = key ;
        mAdresse = adresse ;
    }
    //Les setters sont en protege puisque seul le manager d'offre s'occupe de modifier une offre
    public String getRessourceId() {
        return ressourceId;
    }
    public String getNom() {
        return mNom;
    }
    public String getAdresse(){
        return mAdresse ;
    }
    protected void setAdresse(String addr) {
        this.mAdresse = addr;
    }
    protected void setNom(String nom) {
        this.mNom = nom;
    }
    public String getDescription() {
        return mDescription;
    }
    protected void setDescription(String description) {
        this.mDescription = description;
    }
}
