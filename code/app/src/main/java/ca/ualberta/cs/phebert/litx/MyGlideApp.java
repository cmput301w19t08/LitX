package ca.ualberta.cs.phebert.litx;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

/**
 * This class is simply for loading an image out of Firebase Storage using Glide, it is a required
 * class by Glide and was gotten from Firebase Storage site
 * @author sdupasqu
 * @version 1.0
 * @see ViewPhotoActivity
 */
@GlideModule
public class MyGlideApp extends AppGlideModule {

    /**
     * Used to load the image into the imageview, this function has been re-used from the glide
     * tutorial, see reuse statement on github
     * @param context activity context
     * @param glide glide module
     * @param registry
     */
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(StorageReference.class, InputStream.class,
                new FirebaseImageLoader.Factory());
    }

}
