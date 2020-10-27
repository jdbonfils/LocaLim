package com.example.localim;

import com.example.localim.gestionnaireOffre.ListDOffre;
import com.example.localim.gestionnaireOffre.Offre;

import java.util.ArrayList;
//Methode à implementer dans l'activité qui veut recevoir la lite des offres
//L'activité doit appeler receiveListOffre
public interface OnTaskCompleted {
    void onTaskCompleted(final ListDOffre OffreList);
}
