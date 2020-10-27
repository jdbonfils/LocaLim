package com.example.localim.gestionnaireOffre;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.example.localim.ModifyOffreActivity;
import com.example.localim.OnTaskCompleted;
import com.example.localim.R;
import com.example.localim.gestionnaireOffre.Offre;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
/*
l'OffreManager s'occupe de créer, modifier et supprimer les offres en BD,
 il gere aussi les images liées aux offres, Il transmet les liste d'offre et les offre aux activité
 */
public class OffreManager  {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseTracks;
    private String userID;
    private Context monContext ;
    private OnTaskCompleted listener ;

    public OffreManager(FirebaseAuth fbAuth,OnTaskCompleted obj, Context context)
    {
        monContext = context ;
        listener = obj ;
        mAuth = fbAuth;
        userID = mAuth.getCurrentUser().getUid() ;
        Log.w("TAG","User : "+userID) ;
        //Chaque utilisateur a sa propre liste des produits
        databaseTracks = FirebaseDatabase.getInstance().getReference().child(userID) ;
    }
    //Permet de recevoir la liste d'offre
    //La classe appelant cette methode doit implementer l'interface OntaskCompleted
    //Et gerer la reception des données dans la methode onTaskCompleted() a implementer
    public void receiveListOffre()
    {
        DatabaseReference phoneQuery = databaseTracks.child("ListeProduit");
        phoneQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListDOffre offresList = new ListDOffre(monContext) ;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Offre o = singleSnapshot.getValue(Offre.class);
                    offresList.getmaListe().add(o);
                }
                listener.onTaskCompleted(offresList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });
    }

    //Permet de créer une offre et de l'enregistrer en BD
    public String createAndRegisterOffre(String titre,String description,String adresse)
    {
        String key = databaseTracks.child("ListeProduit").push().getKey() ;
        Offre o = new Offre(titre,description,adresse, key) ;
        databaseTracks.child("ListeProduit").child(key).setValue(o);
        return key ;
    }

    //Permet de créer une offre et de l'enregistrer en BD et d'enregistrer son image associé dans le cloud
    //L'image aura pour nom la valeur de la cle primaire de l'offre en BD
    public String createAndRegisterOffre(String titre,String description,String adresse,Uri ficSelected)
    {
        String key = databaseTracks.child("ListeProduit").push().getKey() ;
        Offre o = new Offre(titre,description,adresse, key) ;
        databaseTracks.child("ListeProduit").child(key).setValue(o);
        if(ficSelected !=  null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/");
            StorageReference mountainImagesRef = storageRef.child(key);
            mountainImagesRef.putFile(ficSelected);
        }
        return key ;
    }

    //Permet de supprimer une offre à partir d'une ressourceID d'une offre
    public void deleteOffre(String key)
    {
        databaseTracks.child("ListeProduit").child(key).removeValue() ;
        //Supprime l'image associé si elle existe
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = storageRef.child("images/" + key);
        pathReference.delete() ;
        File localFile = new File(monContext.getFilesDir(), key);
        //On supprime l'image associé à l'offre qui à pu etre téléchargé
        if(localFile.exists())
        {
            localFile.delete() ;
        }
    }

    //Permet de modifier une offre en BD grace au ressourceID
    public void modifyOffre(String titre,String description,String adresse , String Idressource)
    {
        Offre o = new Offre(titre,description,adresse,Idressource) ;
        databaseTracks.child("ListeProduit").child(Idressource).setValue(o) ;
    }
    //Permet de modifier une offre en BD et l'image associé grace au ressourceID
    public void modifyOffre(String titre,String description,String adresse , Uri ficSelected, String Idressource)
    {
        Offre o = new Offre(titre,description,adresse,Idressource) ;
        databaseTracks.child("ListeProduit").child(Idressource).setValue(o) ;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/");
        StorageReference mountainImagesRef = storageRef.child(Idressource);
        mountainImagesRef.putFile(ficSelected);
    }

    //Permet de recperer l'image dans le cloud à partir du ressourceID de l'offre
    public Bitmap getBitmapFromKey(String key)
    {
        //On recupere le fichier depuis le cloud et on le converti en Bitmap
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = storageRef.child("images/" + key);
        final File localFile = new File(monContext.getFilesDir(), key);
        pathReference.getFile(localFile);
        Bitmap bitmap = null ;
        String filePath = localFile.getPath();
            bitmap = BitmapFactory.decodeFile(filePath);
            //Si pas d'image alors on affiche un icon par defaut
            if(bitmap == null) {
                //Si l'offre n'as pas d'image associé alors on affiche une image par defaut
                return  BitmapFactory.decodeResource(monContext.getResources(), R.mipmap.no_images);
            }
            return  bitmap ;
    }
}

