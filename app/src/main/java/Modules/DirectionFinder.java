package Modules;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import Chord.FileEntry;
import Chord.Pair;

public class DirectionFinder extends FileEntry {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyDnwLF2-WfK8cVZt9OoDYJ9Y8kspXhEHfI";
    private DirectionFinderListener listener;

    List<Pair> pairs;

    public DirectionFinder(DirectionFinderListener listener, List<Pair> pairs) {
        this.listener = listener;
        this.pairs = pairs;
    }

    public List<FileEntry> execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();

        List<FileEntry> resultList = new ArrayList<>();

        //TODO create loop pou na kanei polla download raw data ena gia kathe pair
        for(Pair pair: pairs) {
            //do what execute was doing
            String origin = pair.getOrigin();
            String destination = pair.getDestination();

            String fileData = null;
            try {
                fileData = new DownloadRawData().execute(createUrl(pair)).get().get(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            FileEntry commitFile = new FileEntry(new File(origin+"_"+destination), fileData, origin, destination);
            resultList.add(commitFile);
        }

        return resultList;

        /*String fileData = null;
        try {
            fileData = String.valueOf(new DownloadRawData().execute(createUrl()).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        FileEntry commitFile = new FileEntry(new File(origin+"_"+destination), fileData, origin, destination);
        return commitFile;*/
    }


    //tha pairnei orisma ena Pair object
    //opote an xreiastei tha kaneis polla commit File (mesa sti loopa tis execute)
    private String createUrl(Pair pair) throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(pair.getOrigin(), "utf-8");
        String urlDestination = URLEncoder.encode(pair.getDestination(), "utf-8");

        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY;
    }

    public class DownloadRawData extends AsyncTask<String, Void, List<String>> {

        @Override
        public List<String> doInBackground(String... params) {

            List<String> resultList = new ArrayList();
            String link = params[0];

            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                resultList.add(buffer.toString());
                //return resultList;
                is.close();
                reader.close();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultList;
        }

        @Override
        protected void onPostExecute(List<String> resultList) {
            try {

                Util.parseJSon(listener, resultList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}