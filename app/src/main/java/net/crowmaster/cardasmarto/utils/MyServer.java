package net.crowmaster.cardasmarto.utils;

/**
 * Created by root on 6/7/16.
 */

import android.os.Handler;
import android.util.Log;

import net.crowmaster.cardasmarto.nanohttpdServer.NanoHTTPD;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyServer extends NanoHTTPD {
    private final static int PORT = 37842;
    private static int time = 1;
    private ResponseInterface responseInterface;
    public interface ResponseInterface {
        void OnResponse(JSONObject response);
    }

//    Handler mHandler = new Handler();
//    Runnable mRunnable = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                receiveTestData();
//            } catch (JSONException e) {
//                Log.e("MyServer", "receiveTestData: " + e.getMessage() );
//            }
//            mHandler.postDelayed(mRunnable, 40);
//        }
//    };

    private void receiveTestData() throws JSONException {
        Random rand = new Random(time);
        JSONObject mJson = new JSONObject();
        mJson.put("AcX", (long)(rand.nextInt(6000-2000) + 2000));
        mJson.put("AcY", (long)(rand.nextInt(6000-2000) + 2000));
        mJson.put("AcZ", (long)(rand.nextInt(6000-2000) + 2000));
        mJson.put("Encode1", (long)(rand.nextInt(600-200) + 200));
        mJson.put("Encode2", (long)(rand.nextInt(600-200) + 200));
//        mJson.put("battery", (long)(rand.nextInt(100-20) + 20));
        mJson.put("time", time);
        time +=1;


        responseInterface.OnResponse(mJson);
    }

    public MyServer(ResponseInterface responseInterface) throws IOException {
        super(PORT);
        this.responseInterface = responseInterface;

        Log.e("MyServer", "\nRunning! Point your browsers to http://" +
                getWifiApIpAddress() + ":8080/ \n" );
        //For test purpose only
//        mRunnable.run();
    }

    @Override
    public Response serve(IHTTPSession session) throws JSONException {
        //Log.e("Tag", "request received!");
        Map<String, String> files = new HashMap<String, String>();
        Method method = session.getMethod();
        if (Method.POST.equals(method) || Method.PUT.equals(method)) {
            try {
                session.parseBody(files);
            } catch (IOException ioe) {
                try {
                    return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("UnsuppoEncodException", e.getMessage());
                }
            } catch (ResponseException re) {
                try {
                    return new Response(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("UnsuppoEncodException", re.getMessage());
                }
            }
        }

        // get the POST body
        //String postBody = session.getQueryParameterString();
        //Log.e("postBody", "a" + postBody);
        // or you can access the POST request's parameters
        //String postParameter = session.getParms().get("key_0");

        final JSONObject json = new JSONObject(files.get("postData"));
        Log.e("MyData", json.toString());
        responseInterface.OnResponse(json);
        return newFixedLengthResponse("Good job Rozhina");
    }

    public String getWifiApIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            Log.d("mTag", inetAddress.getHostAddress());
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("mTag", ex.toString());
        }
        return null;
    }

}

