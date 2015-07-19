package net.icedeer.abysmli.iasanalyse;

import android.graphics.drawable.Drawable;

/**
 * Created by abysmli on 18.06.15.
 */
public class ComponentsContainer {
    public final Drawable icon;       // the drawable for the ListView item ImageView
    public final String title;        // the text for the ListView item title
    public final String description;  // the text for the ListView item description

    public ComponentsContainer(Drawable icon, String title, String description) {
        this.icon = icon;
        this.title = title;
        this.description = description;
    }
}
