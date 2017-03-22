package fr.univ_lille1.iut_info.caronic.mapsv3.maps.adapter;

import java.util.List;

/**
 * Created by Master on 16/03/2017.
 */

public class DownloadedItemModel {

    private String title;
    private String absolutePath;
    private int size;

    public DownloadedItemModel(String title, String absolutePath, int size) {
        this.title = title;
        this.absolutePath = absolutePath;
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static int getOccurrencesOfTitleInList(String title, List<DownloadedItemModel> list) {
        int occ = 0;
        for (DownloadedItemModel item : list) {
            if (item.getTitle().equals(title)) {
                occ++;
            }
        }
        return occ;
    }
}
