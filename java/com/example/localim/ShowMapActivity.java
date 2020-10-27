package com.example.localim;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.example.localim.authentificationActivities.LoginActivity;
import com.example.localim.gestionnaireOffre.Offre;

import java.util.ArrayList;

public class ShowMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        //On recupere la liste des offres
        Intent intent = getIntent();
        ArrayList<Offre> listOffre = (ArrayList<Offre>) intent.getBundleExtra("Bundle").getSerializable("OFFRELIST") ;

        //On passe la liste des offre au fragment
        final Fragment fragmentUn = new MapsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Type",1);
        bundle.putSerializable("OFFRELIST", listOffre);
        fragmentUn.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.frameMap, fragmentUn).commit();
    }
    @Override
    public void onBackPressed() {
        //On revient au menu pour s'enregistrer
        startActivity(new Intent(getApplicationContext(), DisplayListActivity.class));
        finish() ;
    }
}