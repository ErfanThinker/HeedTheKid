
package net.crowmaster.cardasmarto.utils;

/**
 * Created by M. Erfan Mowlaei 2016
 */

import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;


public class SDManager {
	private static final String recordsPath = Environment.getExternalStorageDirectory()
			.getAbsolutePath()+File.separator + "Car da smarto" + File.separator + "Cache";
	
	
	public File findFile(File dir, String name) {
	    File[] children = dir.listFiles();
	    try{
	        for(File child : children) {
	            if(child.isDirectory()) {
	               File found = findFile(child, name);
	               if(found != null) return found;
	            } else {
	                if(name.equals(child.getName())) return child;
	            }
	        }
	    }catch(Exception e){
	        //ignore here because we have no access to this folder
	        return null;
	    }

	    return null;
	}
	
	public File findFileInOneDir(File dir, String name) {
	    File[] children = dir.listFiles();
	    try{
	        for(File child : children) {
	            if(!child.isDirectory() && name.equals(child.getName())) {
	            	return child;
	            }
	        }
	    }catch(Exception e){
	        //ignore here because we have no access to this folder
	        return null;
	    }

	    return null;
	}
	
	
	
	public boolean deleteDirectory(File path) throws Exception {
	    if( path.exists() ) {
	    	try{
	    		File[] files = path.listFiles();
	    		for(int i=0; i<files.length; i++) {
	    			if(files[i].isDirectory()) {
	    				deleteDirectory(files[i]);
	    			}
	    			else {
	    				files[i].delete();
	    			}
	    		}
	    	} catch (Exception e) {
				throw e;
			}
	    }
	    return(path.delete());
	  }

	public String ReadTxtFile(String externalSdPath,String fileName){
		StringBuffer stringBuffer2=new StringBuffer();;
		String inputString2;
		BufferedReader inputReader2;
		try{
		    File file = new File(externalSdPath+File.separator,fileName);
		    inputReader2 = new BufferedReader(
		            new InputStreamReader(new FileInputStream(file)));
		    while ((inputString2 = inputReader2.readLine()) != null) {
		        	stringBuffer2.append(inputString2 + "\n");
		        
		    }
		    inputReader2.close();
			} 
		catch (Exception e) {
			}
		return stringBuffer2.toString();
	}
	public void mkDIr(String pathName,String name){
		File outputParenFile = new File(pathName +
				File.separator + name + File.separator);
//		Log.e("FilePath",outputParenFile.getAbsolutePath().substring(0,
//				outputParenFile.getAbsolutePath().lastIndexOf(File.separator))+ 
//				File.separator);
		if(!outputParenFile.exists())
			outputParenFile.mkdirs();
	}
	public int getChildNum(String path){
		File[] list;
		final File files = new File(path+File.separator);
		if (files.exists()){
			list=files.listFiles();
			Arrays.sort(list);
			return list.length;
		}
		return 0;
	}

	public ArrayList<File> getChildren(String path){
		File[] list;
		ArrayList<File> items=new ArrayList<File>();
		final File files = new File(path+File.separator);
		if (files.exists()){
			list=files.listFiles();
			Arrays.sort(list);
			for (File file : list) {
		        if (!file.isDirectory()) {
		        	items.add(file);
		        } 
		    }
			/*for (File file : list) {
		        if (!file.isDirectory() && !file.getName().contains(".info")) {
		        	items.add(file);
		        } 
		    }*/
		}
		return items;
	}
	public ArrayList<String> getChildNames(String path){
		File[] list;
		ArrayList<String> items=new ArrayList<String>();
		final File files = new File(path+File.separator);
		if (files.exists()){
			list=files.listFiles();
			Arrays.sort(list);
			for (File file : list) {
		        if (!file.isDirectory()) {
		        	items.add(file.getName());
		        } 
		    }
			/*for (File file : list) {
		        if (!file.isDirectory()&& !file.getName().contains(".info")) {
		        	items.add(file.getName());
		        } 
		    }*/
		}
		return items;
	}

	public boolean isFile(String path,String fileName){
		File[] list;
		final File files = new File(path+File.separator);
		if (files.exists()){
			list=files.listFiles();
			for (File file : list) {
		        if (file.getName().equals(fileName) && file.isFile()) {
		        	return true;
		        } 
		    }
		}	
		return false;
	}

	public File GetFile(String path,String fileName){
		File[] list;
		final File files = new File(path+File.separator);
		if (files.exists()){
			list=files.listFiles();
			for (File file : list) {
		        if (file.getName().equals(fileName)) {
		        	return file;
		        } 
		    }
		}	
		return null;
	}

	public ArrayList<File> GetFiles(String path){
		File[] list;
		ArrayList<File> items=new ArrayList<File>();
		final File files = new File(path+File.separator);
		if (files.exists()){
			list=files.listFiles();
			Arrays.sort(list);
			for (File file : list) {
		        if (!file.isDirectory()) {
		        	items.add(file);
		        } 
		    }
		}
		return items;
	}

	public ArrayList<String> GetFileNames(String path){
		File[] list;
		ArrayList<String> items=new ArrayList<String>();
		final File files = new File(path+File.separator);
		if (files.exists()){
			list=files.listFiles();
			Arrays.sort(list);
			for (File file : list) {
		        if (!file.isDirectory()) {
		        	items.add(file.getName());
		        } 
		    }
		}
		return items;
	}

    /**
     * use SYstem time for incremental file naming
     * @param path
     * @return
     */
    @Deprecated
	public String getNextFileName(String path){
		String result="Result-";
		ArrayList<String> filenames=this.GetFileNames(path);
		
		for(int i=1; i < Integer.MAX_VALUE; i++){

			if(!filenames.contains(result+Integer.toString(i)+".html")){
				result+=Integer.toString(i)+".html";
				break;
			}
			
		}
		
		return result;
	}

    @Nullable
	public Object[] CreateInfoFile(String currDir,String name){

		File outputParenFile = new File(currDir+ 
				File.separator,name+".html");
		try {
			outputParenFile.createNewFile();
			final FileOutputStream fOut = new FileOutputStream(outputParenFile);
			final OutputStreamWriter myOutWriter =
					new OutputStreamWriter(fOut);
            return new Object[] {fOut, myOutWriter};
			/*myOutWriter.append("");
			myOutWriter.close();
			fOut.getFD().sync();
			fOut.flush();
			fOut.close();*/
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

    public File CreateEmptyInfoFile(String currDir,String name){

        File outputParenFile = new File(currDir+
                File.separator,name+".html");
        try {
            outputParenFile.createNewFile();
            final FileOutputStream fOut = new FileOutputStream(outputParenFile);
            final OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
			myOutWriter.append("");
			myOutWriter.close();
			fOut.getFD().sync();
			fOut.flush();
			fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputParenFile;
    }

	public void WriteInfoToEmptyFile(String CurrDir,String LastName,String NewName,String info){
		File LastFile = new File(CurrDir +
				File.separator, LastName);
		if (LastFile.exists())//first remove last file!
			LastFile.delete();
		File outputParenFile = new File(CurrDir+ 
				File.separator,NewName);
		if (!outputParenFile.exists())
			try {
				outputParenFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		try {
			FileOutputStream fOut = new FileOutputStream(outputParenFile);
			OutputStreamWriter myOutWriter = 
					new OutputStreamWriter(fOut);
			myOutWriter.append(info);
			myOutWriter.close();
			fOut.getFD().sync();
			fOut.flush();
			fOut.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

    /**
     *
     * @param fileName implicitly will be concated with ".html"
     * @return
     */
    @Nullable
 	public JSONArray GetFileInfo(String fileName){
		File[] list;
        JSONArray info = null;
		final File targetFile = new File(recordsPath + File.separator , fileName + ".html");
		if (targetFile.exists()){
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(targetFile));
                String line;
                text.append("[");
                //JSONArray test = new JSONArray("[{\"key0\":1},{\"key1\":2}]");
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append(',');
                }
                text.append("]");
                info = new JSONArray(text.toString());
                br.close();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
                Log.e("SDManager", "Exception in GetFileInfo, IOException:" + e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("SDManager", "Exception in GetFileInfo, JSONException:" + e.getMessage());
            }
        }
		return info;
	}

	public boolean isNotUnique(String prevName, String newName) {
		File[] list;
		final File files = new File(recordsPath +File.separator);
		if (files.exists()){
			list=files.listFiles();
			for (File file : list) {
		        if (file.getName().equals(newName) && !file.getName().equals(prevName)) {
		        	return true;
		        } 
		    }
		}
		
		return false;
	}

	
	/*public void exportDatabse(Context c,String databaseName,String backupName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+c.getPackageName()+"//databases//"+databaseName+"";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(Environment.getExternalStorageDirectory()
            			.getAbsolutePath()+File.separator, backupName);
                if(backupDB.exists()){
                	backupDB.delete();
                	backupDB.createNewFile();
                }
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    
                    Toast mToast=new Toast(c);
        			mToast.setGravity(Gravity.CENTER, 0, 0);
        			
        			TextView mTextView=new TextView(c);
        			mTextView.setPadding(7, 7, 7, 7);
        			mTextView.setGravity(Gravity.CENTER);
        			mTextView.setBackgroundResource(R.drawable.custom_btn_reminders_future);
//        			mTextView.setBackgroundColor(Color.parseColor("#ff5c4f"));
        			mTextView.setTextColor(Color.parseColor("#000000"));
        			mToast.setView(mTextView);
        			mToast.setDuration(Toast.LENGTH_SHORT);
        			
        			mTextView.setText(
        					"A contact backup named \""+backupName+"\" was copied to your SD-card.");
        			mToast.show();
                    
                    
                    
                }
            }else {
            	Toast mToast=new Toast(c);
    			mToast.setGravity(Gravity.CENTER, 0, 0);
    			
    			TextView mTextView=new TextView(c);
    			mTextView.setPadding(7, 7, 7, 7);
    			mTextView.setGravity(Gravity.CENTER);
    			mTextView.setBackgroundResource(R.drawable.custom_btn_reminders_past);
//    			mTextView.setBackgroundColor(Color.parseColor("#ff5c4f"));
    			mTextView.setTextColor(Color.parseColor("#000000"));
    			mToast.setView(mTextView);
    			mToast.setDuration(Toast.LENGTH_SHORT);
    			
    			mTextView.setText(
    					"Couldn't copy contacts into your SD-card, Please check if your SD-card is mounted.");
    			mToast.show();
            }
        } catch (Exception e) {
        	Toast mToast=new Toast(c);
			mToast.setGravity(Gravity.CENTER, 0, 0);
			
			TextView mTextView=new TextView(c);
			mTextView.setPadding(7, 7, 7, 7);
			mTextView.setGravity(Gravity.CENTER);
			mTextView.setBackgroundResource(R.drawable.custom_btn_reminders_past);
//			mTextView.setBackgroundColor(Color.parseColor("#ff5c4f"));
			mTextView.setTextColor(Color.parseColor("#000000"));
			mToast.setView(mTextView);
			mToast.setDuration(Toast.LENGTH_SHORT);
			
			mTextView.setText(
					"An unknown error has occured while trying to copy contacts into your SD-card.");
			mToast.show();
        }
    }

	public void importData(Context c) {
		File backupDB = new File(Environment.getExternalStorageDirectory()
    			.getAbsolutePath()+File.separator, "MarketingAssistant.bu");
		try{
			if(backupDB.exists()){
				DBHelper mDBDbHelper=new DBHelper(c);
				SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(backupDB,null);
				Cursor res =  db.rawQuery( "select * from Customers", null );
				if(res.moveToFirst())
			      while(res.isAfterLast() == false){
			    	  mDBDbHelper.insertContact(res.getString(res.getColumnIndex("CustomerName")),
			    			  res.getString(res.getColumnIndex("CustomerCompany")),
			    			  res.getString(res.getColumnIndex("CustomerTag")),
			    			  res.getString(res.getColumnIndex("CustomerCell1")),
			    			  res.getString(res.getColumnIndex("CustomerCell2")),
			    			  res.getString(res.getColumnIndex("CustomerTel1")),
			    			  res.getString(res.getColumnIndex("CustomerTel2")),
			    			  res.getString(res.getColumnIndex("CustomerFax")),
			    			  res.getString(res.getColumnIndex("CustomerEmail")),
			    			  res.getString(res.getColumnIndex("CustomerWebsite")),
			    			  res.getString(res.getColumnIndex("CustomerAddress")),
			    			  res.getString(res.getColumnIndex("CustomerPostalCode")),
			    			  res.getString(res.getColumnIndex("CustomerDescription")),
			    			  res.getLong(res.getColumnIndex("CustomerCreationDate")));
			    	  res.moveToNext();
				}
//			    res=null;
//			    res =  db.rawQuery( "select * from Events", null );
//				if(res.moveToFirst())
//			      while(res.isAfterLast() == false){
//			    	  Log.e("MyLog","Copy event");
//			    	  mDBDbHelper.insertEvent(res.getString(res.getColumnIndex("EventTitle")),
//			    			  res.getLong(res.getColumnIndex("EventDate")),
//			    			  res.getInt(res.getColumnIndex("CustomerID")),
//			    			  res.getString(res.getColumnIndex("EventType")),
//			    			  res.getInt(res.getColumnIndex("EventSituation")),
//			    			  res.getString(res.getColumnIndex("EventResult")));
//			    	  res.moveToNext();
//				}
				if(res!=null)
					res.close();
			    db.close();
			    mDBDbHelper.close();
			   	Toast mToast=new Toast(c);
	   			mToast.setGravity(Gravity.CENTER, 0, 0);
	   			
	   			TextView mTextView=new TextView(c);
	   			mTextView.setPadding(7, 7, 7, 7);
	   			mTextView.setGravity(Gravity.CENTER);
	   			mTextView.setBackgroundResource(R.drawable.custom_btn_reminders_future);
	//   			mTextView.setBackgroundColor(Color.parseColor("#ff5c4f"));
	   			mTextView.setTextColor(Color.parseColor("#000000"));
	   			mToast.setView(mTextView);
	   			mToast.setDuration(Toast.LENGTH_SHORT);
	   			
	   			mTextView.setText(
	   					"Contacts were successfuly restored.");
	   			mToast.show();
			}else {
				Toast mToast=new Toast(c);
    			mToast.setGravity(Gravity.CENTER, 0, 0);
    			
    			TextView mTextView=new TextView(c);
    			mTextView.setPadding(7, 7, 7, 7);
    			mTextView.setGravity(Gravity.CENTER);
    			mTextView.setBackgroundResource(R.drawable.custom_btn_reminders_past);
//    			mTextView.setBackgroundColor(Color.parseColor("#ff5c4f"));
    			mTextView.setTextColor(Color.parseColor("#000000"));
    			mToast.setView(mTextView);
    			mToast.setDuration(Toast.LENGTH_SHORT);
    			
    			mTextView.setText(
    					"Couldn't find the file: \"MarketingAssistant.bu\" in main directory of your SD-card.");
    			mToast.show();
			}
		}catch(Exception e){
			Toast mToast=new Toast(c);
			mToast.setGravity(Gravity.CENTER, 0, 0);
			
			TextView mTextView=new TextView(c);
			mTextView.setPadding(7, 7, 7, 7);
			mTextView.setGravity(Gravity.CENTER);
			mTextView.setBackgroundResource(R.drawable.custom_btn_reminders_past);
//			mTextView.setBackgroundColor(Color.parseColor("#ff5c4f"));
			mTextView.setTextColor(Color.parseColor("#000000"));
			mToast.setView(mTextView);
			mToast.setDuration(Toast.LENGTH_SHORT);
			
			mTextView.setText(
					"An unknown error has occured while trying to restore data.");
			mToast.show();
		}
		
		
	}*/
	
}
