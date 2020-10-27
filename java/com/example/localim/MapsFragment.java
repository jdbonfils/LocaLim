package com.example.localim;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.localim.gestionnaireOffre.Offre;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
   private String adresse;
    private String titre ;
    private int typeAffichage ;
    private ArrayList<Offre> listDOffre ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        typeAffichage = getArguments().getInt("Type");
        //Si le type est egale à 1 alors la map est affiche dans l'activité detailsOffreactivity
        //Il faut seulement placer l'offre selectionne sur la map
        if(typeAffichage == 0) {
            adresse = getArguments().getString("Adresse");
            titre = getArguments().getString("Titre");
        }
        //Si le type est egale à 1 alors la map est a ffiche dans ShowMapActivty
        //Alors il faut afficher toutes les offres de la liste
        else if(typeAffichage == 1)
        {
            listDOffre = (ArrayList<Offre>) getArguments().getSerializable("OFFRELIST") ;
        }
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    //Convertit une adresse en Latlng
    public static LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.isEmpty()) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    //Permet de verifier si une chaine de charactere est convertible en adresse
    public static Boolean isValidAddress(Context context, String addre) {
        Geocoder coder = new Geocoder(context);
        List<Address> address ;
        try {
            address = coder.getFromLocationName(addre, 5);
            if (address == null || address.isEmpty()) {
                return false;
            }
        }
        catch(IOException e){ return false ;}


        return true ;
    }


    //On place un ping sur l'adresse de l'offre
    //On centre la camera sur l'adresse
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //On doit afficher l'offre selectionné sur une map
        if(typeAffichage==0) {
            LatLng pos = MapsFragment.getLocationFromAddress(getContext(), adresse);
            if (pos != null) {
                googleMap.addMarker(new MarkerOptions().position(pos).title(titre));
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(11.0f));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            }
        }
        // On doit afficher toutes les offres sur une map
        else if(typeAffichage == 1)
        {
            //Pour chaque offre
            for(Offre o : listDOffre)
            {
                //Si elle possede une adresse valide on l'affiche sur la map
                if(isValidAddress(getContext(), o.getAdresse())) {
                    LatLng pos = MapsFragment.getLocationFromAddress(getContext(), o.getAdresse());
                    if (pos != null) {
                        googleMap.addMarker(new MarkerOptions().position(pos).title(o.getNom())).setTag(o);
                    }
                }
            }
            //Des que l'on clique sur le titre d'un ping sur la map, on affiche l'offre sur laquel on a cliqué
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {
                    Offre o = (Offre) marker.getTag() ;
                    Intent i = new Intent(getContext(),DetailsOffreActivity.class);
                    Bundle bundle = new Bundle() ;
                    bundle.putSerializable("OffreList", (Serializable) listDOffre);
                    i.putExtra("Bundle",bundle);
                    i.putExtra("OffreIndex",listDOffre.indexOf(o)) ;
                    startActivity(i);
                }
            });
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(9.0f));
            //Coordonnes du limousin
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(45.8327,1.233606)));
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(9.0f));
        }
    }
}