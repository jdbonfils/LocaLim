package com.example.localim.gestionnaireOffre;

import android.content.Context;
import com.example.localim.gestionnaireOffre.Offre;

import java.util.ArrayList;

//L'objet liste d'offre est composé d'une liste d'offre
public class ListDOffre {

    private ArrayList<Offre> maListe ;
    private Context mContext ;

    public ListDOffre(Context context)
    {
        mContext = context ;
        maListe = new ArrayList<>() ;
    }
    public ListDOffre(Context context,ArrayList<Offre> l)
    {
        mContext = context ;
        maListe = l ;
    }
    public ArrayList<Offre> getmaListe()
    {
        return maListe ;
    }
    public void setmaListe(ArrayList<Offre> list)
    {
         maListe = list  ;
    }

}
