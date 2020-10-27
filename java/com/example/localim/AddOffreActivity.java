package com.example.localim;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.graphics.PathUtils;
import com.example.localim.gestionnaireOffre.ListDOffre;
import com.example.localim.gestionnaireOffre.Offre;
import com.example.localim.gestionnaireOffre.OffreManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

public class AddOffreActivity extends AppCompatActivity implements OnTaskCompleted {
    private OffreManager gestionnaireDOffre ;
    private EditText title,description,adresse ;
    private Uri ficSelected ;
    private ImageView image ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offre);
        gestionnaireDOffre = new OffreManager(FirebaseAuth.getInstance(),this,this) ;

        //Image permettant de visualiser la photo importé ou prise
        image = (ImageView)findViewById(R.id.imageImporte);
        image.setVisibility(View.GONE);
    }


    @Override
    public void onTaskCompleted(final ListDOffre list) {}

    //Des qu'on clique sur le bouton ajouter
    public void add(View view) {
        title = (EditText)  findViewById(R.id.title);
        description = (EditText)  findViewById(R.id.description);
        adresse = (EditText)  findViewById(R.id.location);

        //Le titre doit etre compris entre 1 et 35 charactere
        if( (title.getText().toString().length() <= 1 || title.getText().toString().length() > 35)   )
        {
            title.setError( "Le titre doit contenir entre 1 et 35 caractères" );
        }
        else {
            //La description ne doit pas avoir plus de 600 character
            if( description.getText().toString().length() > 600 )
            {
                description.setError("La description est trop longue !");
            }
            else
            {
                if(adresse.getText().toString().length()<= 3)
                {
                    adresse.setError("Veuillez saisir une adresse");
                }
                else
                {
                    //On recuperer la valeur des champs et l'image , le gestionnaire d'offre s'occupe de crée l'offre et de l'enregistrer en BD
                    gestionnaireDOffre.createAndRegisterOffre( title.getText().toString(), description.getText().toString(),adresse.getText().toString(),ficSelected);
                    startActivity(new Intent(getApplicationContext(), DisplayListActivity.class));
                    finish() ;
                }

            }
        }
    }
    @Override
    public void onBackPressed() {
        //On revient à la liste des offre
        startActivity(new Intent(getApplicationContext(),DisplayListActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //Si l'utilisateur a choisi une photo depuis l'espace disque
        if (requestCode == 123 && resultCode == RESULT_OK && intent!= null) {
            //On recupere l'URI de la photo et on la converti en Bitmap pour l'afficher
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
            //On recupere l'image bitmap et on la converti en URI pour pouvoir apres l'enregistrer dansle cloud
            Bundle extras = intent.getExtras();
            Bitmap bm = (Bitmap) extras.get("data");
            image.setImageBitmap(bm);
            image.setVisibility(View.VISIBLE);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bm, "IMG_" + Calendar.getInstance().getTimeInMillis(), null);
            this.ficSelected = Uri.parse(path);
        }
    }
    //Lance l'activite permettant de chosir un hichier
    public void choisirFic(View v)
    {
        Intent intentB = new Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentB, "Select a BitMap file"), 123);
    }
    //Lance l'activite permettant de prendre une photo
    public void prendrePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, 124);
    }

    public void cancel(View view) {
        startActivity(new Intent(getApplicationContext(), DisplayListActivity.class));
        finish() ;
    }
}