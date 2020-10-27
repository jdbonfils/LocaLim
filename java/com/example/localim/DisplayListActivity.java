package com.example.localim;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.localim.authentificationActivities.LoginActivity;
import com.example.localim.gestionnaireOffre.ListDOffre;
import com.example.localim.gestionnaireOffre.Offre;
import com.example.localim.gestionnaireOffre.OffreManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;

public class DisplayListActivity extends AppCompatActivity implements OnTaskCompleted, SearchView.OnQueryTextListener {

    private ListView maListeView;
    private OffreManager gestionnaireDOffre ;
    private CustomListAdapter adapter ;
    private ListDOffre maListeDOffre ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);
        //On creer une listeDOffre qui gere les elements (comme preciser dans le sujet)
        gestionnaireDOffre = new OffreManager(FirebaseAuth.getInstance(),this,this) ;
        //Permet de recevoir la liste des offre grace à la methode à implémenter OnTaskCompleted
        gestionnaireDOffre.receiveListOffre() ;
    }

    //Pour recevoir la liste des offres a travers cette methode, il faut d'abord appeler la methode receiveListOffre
    @Override
    public void onTaskCompleted(final ListDOffre offreList) {
       //Affichage de la Liste
        maListeView = (ListView) findViewById(R.id.listview) ;
        maListeDOffre = offreList ;
        //On creer l'adapter permettant d'afficher les offres
        adapter = new CustomListAdapter(this, offreList) ;
        maListeView.setAdapter(adapter);

        //Pour chaque offre si on clique dessu :
        maListeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                //On recuepre l'offre selectionne , on passe à l'activité suivante l'indexe de l'offre dans la liste et la liste
                Offre o = (Offre) maListeView.getItemAtPosition(position);
                Intent i = new Intent(getApplicationContext(),DetailsOffreActivity.class);
                Bundle bundle = new Bundle() ;
                bundle.putSerializable("OffreList", (Serializable) offreList.getmaListe());
                i.putExtra("Bundle",bundle);
                i.putExtra("OffreIndex",offreList.getmaListe().indexOf(o)) ;
                startActivity(i);
                finish() ;
            }
        });
        //Si aucune la liste des offre est vide alors on affiche un message pour indiquer à l'utilisatuer de créer une nouvelle offre
        if(adapter.getCount() == 0)
        {
            findViewById(R.id.ajouterOffre).setVisibility(View.VISIBLE);
            findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
        }
    }

    public void addOffre(View view) {
        startActivity(new Intent(getApplicationContext(),AddOffreActivity.class));
        finish() ;
    }
    @Override
    public void onBackPressed() {
        //On revient au menu pour s'enregistrer
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish() ;
    }
    //Creer un menu pour rechercher une offre
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }

    public void displayMap(View view) {
        Intent i = new Intent(getApplicationContext(),ShowMapActivity.class);
        Bundle bundle = new Bundle() ;
        bundle.putSerializable("OFFRELIST", (Serializable) maListeDOffre.getmaListe());
        i.putExtra("Bundle",bundle);
        startActivity(i);
        finish() ;
    }
}