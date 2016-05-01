package com.tresastronautas.trilly;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by juansantiagoacev on 3/2/16.
 */
public class RouteDecoder {

    public RouteDecoder() {
    }

    public static List<LatLng> getRouteFromHex(String hexString) {
        List<LatLng> unencodedList = new ArrayList<>();
        byte[] data = hexStringToByteArray(hexString);
        ArrayList<Map> dictionaries;
        try {
            dictionaries = (ArrayList<Map>) convertFromBytes(data);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        for (Map dic : dictionaries) {
            LatLng l = new LatLng((double) dic.get("lat"), (double) dic.get("lng"));
            unencodedList.add(l);
        }
        return unencodedList;
    }

    private static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}
