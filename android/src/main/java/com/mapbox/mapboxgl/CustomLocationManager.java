package com.mapbox.mapboxgl;

import android.location.Location;
import android.util.Log;
import android.app.PendingIntent;
import android.os.Looper;
import com.mapbox.mapboxsdk.location.engine.LocationEngine;
import com.mapbox.mapboxsdk.location.engine.LocationEngineResult;
import com.mapbox.mapboxsdk.location.engine.LocationEngineCallback;
import com.mapbox.mapboxsdk.location.engine.LocationEngineRequest;
import java.lang.UnsupportedOperationException;
import java.util.List;
import java.util.ArrayList;

/**
 * Custom location manager which wrapps the normally used location engine and thus
 * allows to provide custom locations (eg for mocking).
 * Implements: https://docs.mapbox.com/android/telemetry/api/libcore/3.1.0/com/mapbox/android/core/location/LocationEngine.html
 * Another example of an implemenation of the LocationEngine interface: https://github.com/mapbox/mapbox-navigation-android/blob/main/libnavigation-core/src/main/java/com/mapbox/navigation/core/replay/ReplayLocationEngine.kt
 */
class CustomLocationManager implements LocationEngine {
    /**
     * The location engine that should be used if no custom locations get provided.
     */
    private LocationEngine fallbackLocationEngine;

    /**
     * The latest provided custom location. Also controls, whether the fallback location engine
     * should be used (customLocation == null -> fallbackLocationEngine being used).
     */
    private Location customLocation;

    /**
     * A list for all the callbacks that get called if new locations are available (saved for the case when switches between the custom locations
     * and fallback location engine happen, because then those need to be transfered).
     */
    private List<LocationEngineCallback<LocationEngineResult>> callbacks = new ArrayList<>();

    /**
     * A list for all the requests to location updates (saved for the case when switches between the custom locations
     * and fallback location engine happen, because then those need to be transfered).
     */
    private List<LocationEngineRequest> requests = new ArrayList<>();

    /**
     * Constructor for the CustomLocationManager
     * 
     * @param fallbackLocationEngine The location engine that should be used when no custom locations get provided.
     */
    CustomLocationManager(LocationEngine fallbackLocationEngine){
        this.fallbackLocationEngine = fallbackLocationEngine;
        this.customLocation = null;
    }

    /**
    * Returns the last saved position.
    *
    * @param callback Callback that is getting called after the location is ready.
    */
    @Override
    public void getLastLocation(LocationEngineCallback<LocationEngineResult> callback){
        if (customLocation != null) {
            // If a custom location was provided and should be used, return the custom location.
            callback.onSuccess(LocationEngineResult.create(customLocation));
        } else {
            // Else forward it to the fallback location engine.
            fallbackLocationEngine.getLastLocation(callback);
        }
    }

    /**
    * This method can be used to provide custom locations.
    *
    * @param location Current custom location, if null then the fallback location engine is going to be used.
    */
    public void overrideLastLocation(Location location){
        if (location == null && customLocation != null){
            // Switch to fallback location engine, if null as a custom location gets provided.
            // Add previous callbacks to fallback location engine.
            for (int i = 0; i < this.callbacks.size(); i++) {
                this.fallbackLocationEngine.requestLocationUpdates(this.requests.get(i), this.callbacks.get(i), null);
            }
        }

        if (location != null && customLocation == null){
            // Switch to custom locations.
            // Remove previous callbacks from fallback location engine.
            for (int i = 0; i < this.callbacks.size(); i++) {
                this.fallbackLocationEngine.removeLocationUpdates(this.callbacks.get(i));
            }
        }

        // Save custom location as latest location.
        this.customLocation = location;

        if (this.customLocation != null){
            // If the custom locations should be used, call all the callbacks with the new provided location.
            for (int i = 0; i < this.callbacks.size(); i++) {
                this.callbacks.get(i).onSuccess(LocationEngineResult.create(this.customLocation));
            }
        }
    }

    /**
    * Use this method to unsubscribe from updates of this custom location manager.
    *
    * @param callback This callback won't get called again on future location updates.
    */
    @Override
    public void removeLocationUpdates(LocationEngineCallback<LocationEngineResult> callback){
        if (this.customLocation == null){
            // If the fallback location engine was used forward the removal of the
            // callback to the fallback location engine.
            this.fallbackLocationEngine.removeLocationUpdates(callback);
        }

        final int indexOfCallback = this.callbacks.indexOf(callback);
        this.callbacks.remove(callback);
        this.requests.remove(indexOfCallback);
    }

    /**
    * PendingIntents not supported for custom locations yet, therefore it is being forwarded to the fallback engine.
    *
    * @param pendingIntent 
    */
    @Override
    public void removeLocationUpdates(PendingIntent pendingIntent){
        this.fallbackLocationEngine.removeLocationUpdates(pendingIntent);
    }

    /**
    * Use this method to subscribe for updates of this custom location manager.
    *
    * @param request    LocationEngineRequest for the updates.
    * @param callback   This callback get's called on future location updates.
    * @param looper     The Looper object whose message queue will be used to implement the callback mechanism, or null to invoke callbacks on the main thread (not supported for custom locations).
    */
    @Override
    public void requestLocationUpdates(LocationEngineRequest request, LocationEngineCallback<LocationEngineResult> callback, Looper looper){
        this.callbacks.add(callback);
        this.requests.add(request);
        if (this.customLocation == null){
            // If the fallbackLocationEngine is used forward the request to it.
            this.fallbackLocationEngine.requestLocationUpdates(request, callback, looper);
        }
    }

    /**
    * PendingIntents not supported for custom locations yet, therefore it is being forwarded to the fallback engine.
    *
    * @param request
    * @param pendingIntent 
    */
    @Override
    public void requestLocationUpdates(LocationEngineRequest request, PendingIntent pendingIntent){
        this.fallbackLocationEngine.requestLocationUpdates(request, pendingIntent);
    }
}