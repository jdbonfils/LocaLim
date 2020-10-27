package com.example.localim;

import android.content.Intent;
import android.os.Build;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.example.localim.gestionnaireOffre.ListDOffre;
import com.example.localim.gestionnaireOffre.Offre;
import com.example.localim.gestionnaireOffre.OffreManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class DetailsOffreActivity extends AppCompatActivity implements OnTaskCompleted {

    private Button boutonModify ;
    private TextView titleView, descriptionView, locationView ;
    private ListDOffre offreList ;
    private int offreIndex ;
    private Offre offreSelectionne ;
    private OffreManager gestionnaireDOffre ;
    private ImageView monImageView , noAdrrImg ;
    private FrameLayout layoutMap ;
    private TextView txtNoAddr ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_offre);

        gestionnaireDOffre = new OffreManager(FirebaseAuth.getInstance(),this,this) ;

        //On recuepre les données transimses par l'activite suivante
        Intent intent = getIntent();
        offreIndex = intent.getIntExtra("OffreIndex" , -1);
        try {
            offreList = new ListDOffre(this, (ArrayList<Offre>) intent.getBundleExtra("Bundle").getSerializable("OffreList"));
        }
        catch(Exception e)
        {
            Log.w("TAG","Erreur");
        }
        offreSelectionne = offreList.getmaListe().get(offreIndex);


        layoutMap = (FrameLayout) findViewById(R.id.frameLayout1) ;
        noAdrrImg = (ImageView) findViewById(R.id.noaddrImg) ;
        txtNoAddr =  (TextView) findViewById(R.id.noaddr) ;
        //On creer un nouveau fragment puis on lui transmet l'adresse et le titre d'une offre
        //Si l'adresse est une adresse valide alors on la passe au fragment affichant la carte
        String adresse = offreSelectionne.getAdresse() ;
        if(MapsFragment.isValidAddress(getApplicationContext(),adresse) && adresse != null) {
            final Fragment fragmentUn = new MapsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Type",0);
            bundle.putString("Adresse", offreSelectionne.getAdresse());
            bundle.putString("Titre", offreSelectionne.getNom());
            fragmentUn.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout1, fragmentUn).commit();
            layoutMap.setVisibility(View.VISIBLE);
            noAdrrImg.setVisibility(View.GONE);
            txtNoAddr.setVisibility(View.GONE);
        }
        else//Si non on affiche un message d'erreur
        {
            layoutMap.setVisibility(View.GONE);
            noAdrrImg.setVisibility(View.VISIBLE);
            txtNoAddr.setVisibility(View.VISIBLE);
        }

        //Affectation des boutons
        boutonModify = (Button) findViewById(R.id.buttonModify) ;
        titleView = (TextView) findViewById(R.id.titleView) ;
        descriptionView = (TextView) findViewById(R.id.DescriptionView) ;
        locationView = (TextView) findViewById(R.id.locationView) ;
        monImageView = (ImageView) findViewById(R.id.imageView) ;

        //On affiche les données
        titleView.setText(offreSelectionne.getNom());
        descriptionView.setText(offreSelectionne.getDescription());
        locationView.setText(offreSelectionne.getAdresse());
        monImageView.setImageBitmap(gestionnaireDOffre.getBitmapFromKey(offreList.getmaListe().get(offreIndex).getRessourceId()));
        //Permet de justifier le texte
        descriptionView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        viewGroup.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){
            //Gere le swipe à gauche
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                //Mise a jour de l'interface pour passer à l'offre suivante
                //On ne peut pas swipe right si on est à la premier offre
                if(offreIndex >= 0  ) {
                    //On decremente l'indexe de l'offre courante dans la liste
                    offreIndex--;
                    //Puis on affiche la nouvelle offre
                    titleView.setText(offreList.getmaListe().get(offreIndex).getNom());
                    descriptionView.setText(offreList.getmaListe().get(offreIndex).getDescription());
                    monImageView.setImageBitmap(gestionnaireDOffre.getBitmapFromKey(offreList.getmaListe().get(offreIndex).getRessourceId()));
                    locationView.setText(offreList.getmaListe().get(offreIndex).getAdresse());

                    //Si l'adresse est une adresse valide alors on la passe au fragment affichant la carte
                    String adresse = offreList.getmaListe().get(offreIndex).getAdresse() ;
                    if(MapsFragment.isValidAddress(getApplicationContext(),adresse))
                    {
                        Fragment fragmentUn = new MapsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("Adresse", adresse);
                        bundle.putString("Titre", offreList.getmaListe().get(offreIndex).getNom());
                        fragmentUn.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout1,fragmentUn).commit();
                        layoutMap.setVisibility(View.VISIBLE);
                        noAdrrImg.setVisibility(View.GONE);
                        txtNoAddr.setVisibility(View.GONE);
                    }
                    else//Si non on affiche un message d'erreur
                    {
                        layoutMap.setVisibility(View.GONE);
                        noAdrrImg.setVisibility(View.VISIBLE);
                        txtNoAddr.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Premier element de la liste !",Toast.LENGTH_SHORT).show();
                }
            }
            //Gere le swipe à gauche
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                //On ne peut pas swipe left si on est à la dernière offre
                if(offreIndex < offreList.getmaListe().size()-1  ) {
                    //On incrémente l'indexe de l'offre courante dans la liste
                    //Mise a jour de l'interface pour passer à l'offre précédente
                    offreIndex++;
                    titleView.setText(offreList.getmaListe().get(offreIndex).getNom());
                    descriptionView.setText(offreList.getmaListe().get(offreIndex).getDescription());
                    locationView.setText(offreList.getmaListe().get(offreIndex).getAdresse());
                    monImageView.setImageBitmap(gestionnaireDOffre.getBitmapFromKey(offreList.getmaListe().get(offreIndex).getRessourceId()));

                    //Si l'adresse est une adresse valide alors on la passe au fragment affichant la carte
                    String adresse = offreList.getmaListe().get(offreIndex).getAdresse() ;
                    if(MapsFragment.isValidAddress(getApplicationContext(),adresse))
                    {
                        Fragment fragmentUn = new MapsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("Adresse", adresse);
                        bundle.putString("Titre", offreList.getmaListe().get(offreIndex).getNom());
                        fragmentUn.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout1,fragmentUn).commit();
                        layoutMap.setVisibility(View.VISIBLE);
                        noAdrrImg.setVisibility(View.GONE);
                        txtNoAddr.setVisibility(View.GONE);
                    }
                    else //Si non on affiche un message d'erreur
                    {
                        layoutMap.setVisibility(View.GONE);
                        noAdrrImg.setVisibility(View.VISIBLE);
                        txtNoAddr.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Dernier element de la liste !",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void supprimerOffre(View view) {
        Log.w("Supression : ", ""+offreIndex) ;
        //On demande au gestionnaire d'offre de supprimé une ressource
        gestionnaireDOffre.deleteOffre(offreList.getmaListe().get(offreIndex).getRessourceId());
        startActivity(new Intent(getApplicationContext(),DisplayListActivity.class));
        finish() ;
    }

    //On retourne à la liste des offres
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),DisplayListActivity.class)); finish() ;
    }

    @Override
    public void onTaskCompleted(ListDOffre OffreList) {
    }
    //On retourne à la liste des offres
    public void back(View view) {
        startActivity(new Intent(getApplicationContext(),DisplayListActivity.class));
        finish() ;
    }
    //On passe l'offre selectione à la nouvelle activite
    public void modify(View view) {
        Intent i = new Intent(getApplicationContext(),ModifyOffreActivity.class);
        i.putExtra("Offre",offreList.getmaListe().get(offreIndex)) ;
        startActivity(i);
        finish() ;
    }
}