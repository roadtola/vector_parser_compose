package com.roatola.vectorparsercompose;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Pair;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ${Deven} on 2/1/18.
 * Edited by ${roadtola} on 1.29.23
 * PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
 * myImageView.setColorFilter(porterDuffColorFilter);
 */

class VectorChildFinder {

    private VectorDrawableCompat vectorDrawable;

    /**
     * @param context   Your Activity Context
     * @param vectorRes Path of your vector drawable resource
     * @param imageView ImaveView that are showing vector drawable
     */
    public VectorChildFinder(Context context, int vectorRes, ImageView imageView) {
        vectorDrawable = VectorDrawableCompat.create(context.getResources(),
                vectorRes, null);
        vectorDrawable.setAllowCaching(false);
        imageView.setImageDrawable(vectorDrawable);
    }

    public VectorChildFinder(Context context,int vectorRes) {
        vectorDrawable = VectorDrawableCompat.create(context.getResources(),
                vectorRes, null);
        vectorDrawable.setAllowCaching(false);
    }

    public VectorDrawableCompat getVectorDrawable(){
        return vectorDrawable;
    }

    /**
     * @param pathName Path name that you gave in vector drawable file
     * @return A Object type of VectorDrawableCompat.VFullPath
     */
    public VectorDrawableCompat.VFullPath findPathByName(String pathName) {
        return (VectorDrawableCompat.VFullPath) vectorDrawable.getTargetByName(pathName);
    }

    public Pair<HashMap<String, VectorDrawableCompat.VFullPath>,HashMap<String, VectorDrawableCompat.VGroup>> getAll() {
        ArrayMap<String, Object> res = vectorDrawable.getAllTargets();
        HashMap<String, VectorDrawableCompat.VFullPath> path = new HashMap<>();
        HashMap<String, VectorDrawableCompat.VGroup> group = new HashMap<>();

        for (Map.Entry<String, Object> entry : res.entrySet()) {
            String key = entry.getKey();
            try{
                VectorDrawableCompat.VFullPath value = (VectorDrawableCompat.VFullPath) entry.getValue();
                path.put(key, value);
            }catch (Exception e){
                VectorDrawableCompat.VGroup value = (VectorDrawableCompat.VGroup) entry.getValue();
                group.put(key, value);
            }
        }

        return Pair.create(path,group);
    }

    /**
     * @param groupName Group name that you gave in vector drawable file
     * @return A Object type of VectorDrawableCompat.VGroup
     */
    public VectorDrawableCompat.VGroup findGroupByName(String groupName) {
        return (VectorDrawableCompat.VGroup) vectorDrawable.getTargetByName(groupName);
    }

}
