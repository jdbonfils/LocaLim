package com.example.localim;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.localim.gestionnaireOffre.ListDOffre;
import com.example.localim.gestionnaireOffre.Offre;
import com.example.localim.gestionnaireOffre.OffreManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
//Custom list Adapter permet d'afficher la liste des offres dans un listView
public class CustomListAdapter extends BaseAdapter implements Filterable {

    private ListDOffre  listData;
    private ArrayList<Offre> listReference ;
    private LayoutInflater layoutInflater;
    private Context context;
    private OffreFilter offrefilter ;

    public CustomListAdapter(Context aContext,  ListDOffre listData) {
        this.context = aContext;
        this.listData = listData;
        listReference = listData.getmaListe() ;
        layoutInflater = LayoutInflater.from(aContext);
    }
    @Override
    public int getCount() {
        return listData.getmaListe().size();
    }

    @Override
    public Object getItem(int position) {
        return listData.getmaListe().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Permet de recuperer les données à afficher
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_layout, null);
            holder = new ViewHolder();
            holder.imageOffreView = (ImageView) convertView.findViewById(R.id.imageViewPhoto);
            holder.offreNameView = (TextView) convertView.findViewById(R.id.textViewNom);
            holder.lieuxOffreView = (TextView) convertView.findViewById(R.id.textViewPlace);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.offreNameView.setText(this.listData.getmaListe().get(position).getNom());
        holder.lieuxOffreView.setText("Lieux : " + this.listData.getmaListe().get(position).getAdresse());
        //Permet de recuperer l'image associé à l'offre et de l'affecté à une imageView
        OffreManager gestionnaire = new OffreManager(FirebaseAuth.getInstance(),(OnTaskCompleted) context,context) ;
        holder.imageOffreView.setImageBitmap(gestionnaire.getBitmapFromKey(this.listData.getmaListe().get(position).getRessourceId()));

        return convertView;
    }
    @Override
    public Filter getFilter() {
        if (offrefilter == null) {
            offrefilter = new OffreFilter();
        }
        return offrefilter;
    }

    static class ViewHolder {
        ImageView imageOffreView;
        TextView offreNameView;
        TextView lieuxOffreView;
    }
    //Permet de rechercher une offre
    private class OffreFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();
            //Recherche seulement si l'utilisateur a saisi quelque chose si non on affiche la liste entiere
            if (constraint!=null && constraint.length()>0) {
                ArrayList<Offre> tempList = new ArrayList<Offre>();
                // Pour chaque offre on recherche si le mot saisi par l'utilisateur se trouve aussi dans le titre ou la description de l'offre
                for (Offre offre : listReference) {
                    if ( offre.getAdresse().toLowerCase().contains(constraint.toString().toLowerCase()) || offre.getNom().toLowerCase().contains(constraint.toString().toLowerCase()) || offre.getDescription().toLowerCase().contains(constraint.toString().toLowerCase()) ) {
                        tempList.add(offre);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = listReference.size();
                filterResults.values = listReference;
            }
            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listData.setmaListe((ArrayList<Offre>) results.values);
            notifyDataSetChanged();
        }
    }

}
