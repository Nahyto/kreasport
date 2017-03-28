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
        //Log.d(LOG, "searhing for parcours: " + parcoursId);
        for (Parcours parcours : parcoursList) {
            if (parcours.getId() == parcoursId) {
                return parcours;
            }
        }
        //Log.d(LOG, "did not find");
        return null;
    }

    public void addNew(List<Parcours> parcoursFromDownload) {
        if (parcoursFromDownload != null) {
            for (Parcours parcours : parcoursFromDownload) {
                if (!alreadyHaveParcours(parcours.getId())) {
                    Log.d(LOG, "adding new parcours: " + parcours.getId() + ", " + parcours.getTitle());
                    parcoursList.add(parcours);
                } else {
                    Log.d(LOG, "already have parcours: " + parcours.getId());
                }
            }
        }
    }



    private boolean alreadyHaveParcours(int parcoursId) {
        for (Parcours parcours : parcoursList) {
            if (parcours.getId() == parcoursId) {
                return true;
            }
        }
        return false;
    }

    public void addSpecificParcours(ParcoursList allRestored, int focusOnParcours) {
        Log.d(LOG, "adding specific parcours");
        if (allRestored != null) {

            for (Parcours parcours : allRestored.getParcoursList()) {
                if (parcours.getId() == focusOnParcours) {
                    parcoursList.add(parcours);
                    return;
                }
            }
        } else {
            Log.d(LOG, "couldnt add because parameter was null");
        }
    }
}
