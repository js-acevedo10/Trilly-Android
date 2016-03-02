package com.tresastronautas.trilly;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by juansantiagoacev on 3/2/16.
 */
public class RouteEncoder {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public RouteEncoder() {
    }

    public static String getRouteInHex(List<LatLng> unencodedRoute) {
        Map dictionary;
        ArrayList<Map> latLongArray = new ArrayList<Map>();
        for (LatLng latlong : unencodedRoute) {
            dictionary = new HashMap();
            dictionary.put("lat", latlong.latitude);
            dictionary.put("lng", latlong.longitude);
            latLongArray.add(dictionary);
        }
        byte[] data;
        try {
            data = convertToBytes(latLongArray);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHex(data);
    }

    private static byte[] convertToBytes(ArrayList<Map> toSerialize) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(toSerialize);
            return bos.toByteArray();
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
