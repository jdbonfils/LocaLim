package com.example.localim;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.localim.gestionnaireOffre.ListDOffre;
import com.example.localim.gestionnaireOffre.Offre;
import com.example.localim.gestionnaireOffre.OffreManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class ModifyOffreActivity extends AppCompatActivity implements OnTaskCompleted {

    private Offre offreSelectionne ;
    private EditText titleText, descriptionTexte, adresseText ;
    private OffreManager gestionnaireDOffre ;
    private Uri ficSelected ;
    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_list);

        gestionnaireDOffre = new OffreManager(FirebaseAuth.getInstance(),this,this) ;

        //On recupere l'offre transime par l'activité précédente
        Intent intent = getIntent();
        offreSelectionne = (Offre) intent.getSerializableExtra("Offre") ;
        titleText = (EditText) findViewById(R.id.title) ;
        image = (ImageView) findViewById(R.id.imageImporte) ;
        image.setVisibility(View.GONE);
        descriptionTexte = (EditText)findViewById(R.id.description) ;
        adresseText = (EditText)findViewById(R.id.location) ;
        titleText.setText(offreSelectionne.getNom());
        descriptionTexte.setText(offreSelectionne.getDescription());
        adresseText.setText(offreSelectionne.getAdresse());
        TextView tx = (TextView) findViewById(R.id.modifyTextView) ;
        tx.setText("Modify : "+offreSelectionne.getNom());
    }

    public void modify(View view) {
        String monTitre = titleText.getText().toString() ;
        String description = descriptionTexte.getText().toString() ;
        String ressourceId = offreSelectionne.getRessourceId() ;
        String adresse =  adresseText.getText().toString() ;
        //Le titre saisi doit etre commpris entre 1 et 35
        if( titleText.getText().toString().length() <= 1 || titleText.getText().toString().length() > 35 )
        {
            titleText.setError( "Le titre doit conenir entre 1 et 35 characteres" );
        }
        else {
            //La description ne doit pas depasser les 600 chars
            if (descriptionTexte.getText().toString().length() > 600) {
                descriptionTexte.setError("La description est trop longue !");
            } else {
                //Si aucun fichier n'as ete importé ou pris alors on enregistre l'offre sans image
                if(ficSelected == null)
                {
                    gestionnaireDOffre.modifyOffre(monTitre,description,adresse ,ressourceId) ;
                }
                else
                {
                    gestionnaireDOffre.modifyOffre(monTitre,description,adresse,ficSelected ,ressourceId) ;
                }
                startActivity(new Intent(getApplicationContext(), DisplayListActivity.class));
                finish();
            }
        }
    }

    //On retourne à la liste des offres
    public void cancel(View view) {
        startActivity(new Intent(getApplicationContext(), DisplayListActivity.class));
        finish() ;
    }

    @Override
    public void onTaskCompleted(ListDOffre OffreList) {
    }

    //Permet de receptionné et gérer les donnés transimse par les activité "Prendre une photo" et "Selectionner un fichier"
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //Si l'utilisateur a upload une photo
        if (requestCode == 123 && resultCode == RESULT_OK && intent!= null) {
            //On recupere le fichier URI, on le converti en BitMap et on l'affiche
            this.ficSelected = intent.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),  this.ficSelected);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(bitmap != null) {
                image.setImageBitmap(bitmap);
                image.setVisibility(View.VISIBLE);
            }
        }
        //Si l'utilisateur a pris une photo
        if (requestCode == 124 && resultCode == RESULT_OK && intent!= null) {
            //On recupere le fichier bitmap, on l'affiche et on le converti en URI
            Bundle extras = intent.getExtras();
            Bitmap bm = (Bitmap) extras.get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            //bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            image.setImageBitmap(bm);
            image.setVisibility(View.VISIBLE);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bm, "IMG_" + Calendar.getInstance().getTimeInMillis(), null);
            this.ficSelected = Uri.parse(path);
        }
    }

    @Override
    public void onBackPressed() {
        //On revient au menu pour s'enregistrer
        startActivity(new Intent(getApplicationContext(),DisplayListActivity.class));
    }

    //"Selectionner un fichier"
    public void choisirFic(View v)
    {
        Intent intentB = new Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentB, "Select a BitMap file"), 123);
    }

    //"Prendre une photo"
    public void prendrePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, 124);
    }
}