//package net.crowmaster.cardasmarto.utils;
//
//
//import android.os.Build;
//import android.util.Base64;
//import android.util.Log;
//
//import org.ksoap2.SoapEnvelope;
//import org.ksoap2.serialization.PropertyInfo;
//import org.ksoap2.serialization.SoapObject;
//import org.ksoap2.serialization.SoapSerializationEnvelope;
//import org.ksoap2.transport.HttpTransportSE;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
////import org.ksoap2.transport.AndroidHttpTransport;
//
//
//public class WebserviceCall extends Thread {
//
//
//    /**
//     * Variable Decleration................
//     */
//    String namespace = "http://tempuri.org/";
//    //private String url = "http://172.16.146.215:8080/mood/ut_mood_wsrv.asmx"; dosent accessable out of uni
//    private String url = "http://aris.ut.ac.ir:80/mood/ut_mood_wsrv.asmx";
//    //private String url = "http://utmood-001-site1.btempurl.com/ut_mood_wsrv.asmx";
//    String SOAP_ACTION;
//    SoapObject request = null, objMessages = null;
//    SoapSerializationEnvelope envelope;
//    HttpTransportSE androidHttpTransport;
//
//    public WebserviceCall() {
//    }
//
//
//    /**
//     * Set Envelope
//     */
//    protected void SetEnvelope() {
//
//        try {
//
//            // Creating SOAP envelope
//            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//
//            //You can comment that line if your web service is not .NET one.
//            envelope.dotNet = true;
//
//            envelope.setOutputSoapObject(request);
//            androidHttpTransport = new HttpTransportSE(url);
//            androidHttpTransport.debug = true;
//
//        } catch (Exception e) {
//            System.out.println("Soap Exception---->>>" + e.toString());
//        }
//    }
//
//
////    public String test()
////
////    {
////
////        UserConfig.loadConfig();
////        String ID = String.valueOf(UserConfig.getCurrentUser().id);
////
////        String MethodName = "sendtest";
////        try {
////            SOAP_ACTION = namespace + MethodName;
////
////            //Adding values to request object
////            request = new SoapObject(namespace, MethodName);
////            //Adding string value to request object
////            PropertyInfo P_RSID = new PropertyInfo();
////            P_RSID.setName("RSID");
////            P_RSID.setValue(ID);
////            P_RSID.setType(String.class);
////            request.addProperty(P_RSID);
////
////
////            SetEnvelope();
////
////            try {
////
////                //SOAP calling webservice
////                androidHttpTransport.call(SOAP_ACTION, envelope);
////
////                //Got Webservice response
////                String result = envelope.getResponse().toString();
////
////                return result;
////
////            } catch (Exception e) {
////                // TODO: handle exception
////                return e.toString();
////            }
////        } catch (Exception e) {
////            // TODO: handle exception
////            return e.toString();
////        }
////    }
//
//    ///////////////////////////////////////////////
//    public String upload(String path, String filename, String username)
//
//    {
//        Log.i("Upload", "Started --> " + filename );
//        //zip file
//        String[] a = new String[1];
//        a[0] = path + "/" + filename;
//        String zipname = filename.substring(0, filename.length()-3) + "zip";
//        zip(a, path + "/" + zipname);
//
//        //File file = new File(path, filename);
//        File file = new File(path, zipname);
//        int size = (int) file.length();
//        byte[] bytes = new byte[size];
//        Log.i("bbbb", String.valueOf(size));
//        try {
//            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
//            buf.read(bytes, 0, bytes.length);
//            buf.close();
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//
//
//
//        String MethodName = "UploadFile";
//        String fbs = Base64.encodeToString(bytes, Base64.DEFAULT);
//        try {
//            SOAP_ACTION = namespace + MethodName;
//
//            //Adding values to request object
//            request = new SoapObject(namespace, MethodName);
//            //Adding string value to request object
//            PropertyInfo P_File = new PropertyInfo();
//            P_File.setName("fbs");
//            P_File.setValue(fbs);
//            P_File.setType(String.class);
//
//
//            PropertyInfo P_Name = new PropertyInfo();
//            P_Name.setName("fileName");
//            P_Name.setValue(zipname);
//            P_Name.setType(String.class);
//
//            PropertyInfo P_Size = new PropertyInfo();
//            P_Size.setName("SIZE");
//            P_Size.setValue(size);
//            P_Size.setType(Integer.class);
//
//            PropertyInfo p_username = new PropertyInfo();
//            p_username.setName("username");
//            p_username.setValue(username);
//            p_username.setType(String.class);
//
//            request.addProperty(P_File);
//            request.addProperty(P_Name);
//            request.addProperty(P_Size);
//            request.addProperty(p_username);
//            SetEnvelope();
//
//            try {
//
//                //SOAP calling webservice
//                androidHttpTransport.call(SOAP_ACTION, envelope);
//
//                //Got Webservice response
//                String result = envelope.getResponse().toString();
//
//                return result;
//
//            } catch (Exception e) {
//                // TODO: handle exception
//                return e.toString();
//            }
//        } catch (Exception e) {
//            // TODO: handle exception
//            return e.toString();
//        }
//    }
//
//
//    public void zip(String[] _files, String zipFileName)
//    {
//        final int BUFFER = 10000000 ;
//        try {
//            BufferedInputStream origin = null;
//
//            File yourFile = new File(_files[0]);
//            if(!yourFile.exists()) {
//                yourFile.createNewFile();
//            }
//            FileOutputStream dest = new FileOutputStream(zipFileName,true);
//            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
//                    dest));
//            byte data[] = new byte[BUFFER];
//
//            for (int i = 0; i < _files.length; i++) {
//                Log.v("Compress", "Adding: " + _files[i]);
//                FileInputStream fi = new FileInputStream(_files[i]);
//                origin = new BufferedInputStream(fi, BUFFER);
//
//                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
//                out.putNextEntry(entry);
//                int count;
//
//                while ((count = origin.read(data, 0, BUFFER)) != -1) {
//                    out.write(data, 0, count);
//                }
//                origin.close();
//            }
//
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    ///////////////////////////////////////////////////
//    public String register_user(String ID, String Username, String Phone, String firstname, String lastname)
//
//    {
//
//        String MethodName = "Register_User";
//        try {
//            SOAP_ACTION = namespace + MethodName;
//
//            //Adding values to request object
//            request = new SoapObject(namespace, MethodName);
//
//            //Adding string value to request object
//            PropertyInfo P_ID = new PropertyInfo();
//            P_ID.setName("ID");
//            P_ID.setValue(ID);
//            P_ID.setType(String.class);
//            request.addProperty(P_ID);
//
//            PropertyInfo P_Username = new PropertyInfo();
//            P_Username.setName("Username");
//            P_Username.setValue(Username);
//            P_Username.setType(String.class);
//            request.addProperty(P_Username);
//
//            PropertyInfo P_Phone = new PropertyInfo();
//            P_Phone.setName("Phone");
//            P_Phone.setValue(Phone);
//            P_Phone.setType(String.class);
//            request.addProperty(P_Phone);
//
//            PropertyInfo P_firstname = new PropertyInfo();
//            P_firstname.setName("firstname");
//            P_firstname.setValue(firstname);
//            P_firstname.setType(String.class);
//            request.addProperty(P_firstname);
//
//            PropertyInfo P_lastname = new PropertyInfo();
//            P_lastname.setName("lastname");
//            P_lastname.setValue(lastname);
//            P_lastname.setType(String.class);
//            request.addProperty(P_lastname);
//
//            PropertyInfo p_devman = new PropertyInfo();
//            p_devman.setName("deviceman");
//            p_devman.setValue(Build.MANUFACTURER);
//            p_devman.setType(String.class);
//            request.addProperty(p_devman);
//
//            PropertyInfo p_devname = new PropertyInfo();
//            p_devname.setName("devicename");
//            p_devname.setValue(Build.MODEL);
//            p_devname.setType(String.class);
//            request.addProperty(p_devname);
//
//            PropertyInfo p_devSDK = new PropertyInfo();
//            p_devSDK.setName("SDKver");
//            p_devSDK.setValue(String.valueOf(Build.VERSION.SDK_INT));
//            p_devSDK.setType(String.class);
//            request.addProperty(p_devSDK);
//
//
//
//            SetEnvelope();
//
//            try {
//
//                //SOAP calling webservice
//                androidHttpTransport.call(SOAP_ACTION, envelope);
//
//                //Got Webservice response
//                String result = envelope.getResponse().toString();
//                Log.i("wertyuiopoiuytrew", result);
//                return result;
//
//            } catch (Exception e) {
//                // TODO: handle exception
//                return e.toString();
//            }
//        } catch (Exception e) {
//            // TODO: handle exception
//            return e.toString();
//        }
//    }
//
////    public String Check_call()
////
////    {
////
////        UserConfig.loadConfig();
////        String ID = String.valueOf(UserConfig.getCurrentUser().id);
////
////        String MethodName = "Check_call";
////        try {
////            SOAP_ACTION = namespace + MethodName;
////
////            //Adding values to request object
////            request = new SoapObject(namespace, MethodName);
////            //Adding string value to request object
////            PropertyInfo P_ID = new PropertyInfo();
////            P_ID.setName("ID");
////            P_ID.setValue(ID);
////            P_ID.setType(String.class);
////            request.addProperty(P_ID);
////
////
////            SetEnvelope();
////
////            try {
////
////                //SOAP calling webservice
////                androidHttpTransport.call(SOAP_ACTION, envelope);
////
////                //Got Webservice response
////                String result = envelope.getResponse().toString();
////
////                return result;
////
////            } catch (Exception e) {
////                // TODO: handle exception
////                return e.toString();
////            }
////        } catch (Exception e) {
////            // TODO: handle exception
////            return e.toString();
////        }
////    }
//    public String upload_Temp(String path, String filename, String username)
//
//    {
//        Log.i("Upload_Temp", "Started --> " + filename );
//        //zip file
//        String[] a = new String[1];
//        a[0] = path + "/" + filename;
//        String zipname = filename.substring(0, filename.length()-3) + "zip";
//        zip(a, path + "/" + zipname);
//
//        //File file = new File(path, filename);
//        File file = new File(path, zipname);
//        int size = (int) file.length();
//        byte[] bytes = new byte[size];
//        Log.i("bbbb", String.valueOf(size));
//        try {
//            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
//            buf.read(bytes, 0, bytes.length);
//            buf.close();
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//
//
//
//        String MethodName = "Upload_Temprary";
//        String fbs = Base64.encodeToString(bytes, Base64.DEFAULT);
//        try {
//            SOAP_ACTION = namespace + MethodName;
//
//            //Adding values to request object
//            request = new SoapObject(namespace, MethodName);
//            //Adding string value to request object
//            PropertyInfo P_File = new PropertyInfo();
//            P_File.setName("fbs");
//            P_File.setValue(fbs);
//            P_File.setType(String.class);
//
//
//            PropertyInfo P_Name = new PropertyInfo();
//            P_Name.setName("fileName");
//            P_Name.setValue(zipname);
//            P_Name.setType(String.class);
//
//            PropertyInfo P_Size = new PropertyInfo();
//            P_Size.setName("SIZE");
//            P_Size.setValue(size);
//            P_Size.setType(Integer.class);
//
//            PropertyInfo p_ID = new PropertyInfo();
//            p_ID.setName("ID");
//            p_ID.setValue(username);
//            p_ID.setType(String.class);
//
//            request.addProperty(P_File);
//            request.addProperty(P_Name);
//            request.addProperty(P_Size);
//            request.addProperty(p_ID);
//            SetEnvelope();
//
//            try {
//
//                //SOAP calling webservice
//                androidHttpTransport.call(SOAP_ACTION, envelope);
//
//                //Got Webservice response
//                String result = envelope.getResponse().toString();
//
//                return result;
//
//            } catch (Exception e) {
//                // TODO: handle exception
//                return e.toString();
//            }
//        } catch (Exception e) {
//            // TODO: handle exception
//            return e.toString();
//        }
//    }
//
//
//
//
//
//
//
//
//}