package fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by Master on 26/03/2017.
 */

public class ParcoursList {

    private static final String LOG = ParcoursList.class.getSimpleName();


    private List<Parcours> parcoursList;

    public ParcoursList() {
        parcoursList = new ArrayList<>();
    }

    public List<Parcours> getParcoursList() {
        return parcoursList;
    }

    public void setParcoursList(List<Parcours> parcoursList) {
        this.parcoursList = parcoursList;
    }


    public void addAll(List<Parcours> newParcoursList) {
        parcoursList.addAll(newParcoursList);
    }

    public Parcours get(int parcoursId) {
        return parcoursList.get(parcoursId);
    }

    public Parcours getParcoursById(int parcoursId) {
        for (Parcours parcours : parcoursList) {
            if (parcours.getId() == parcoursId) {
                return parcours;
            }
        }
        return null;
    }
}
