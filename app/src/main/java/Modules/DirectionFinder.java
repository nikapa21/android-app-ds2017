package Modules;

import android.os.AsyncTask;

import org.json.JSONException;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import Chord.FileEntry;
import Chord.Pair;

public class DirectionFinder extends FileEntry {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyDnwLF2-WfK8cVZt9OoDYJ9Y8kspXhEHfI";
    private DirectionFinderListener listener;
    private Map<Pair, String> pairCachedDestinations;
    private List<Pair> nonExistingPairs;

    public DirectionFinder(DirectionFinderListener listener, List<Pair> nonExistingPairs, Map<Pair, String> pairCachedDestinations) {
        this.listener = listener;
        this.nonExistingPairs = nonExistingPairs;
        this.pairCachedDestinations = pairCachedDestinations;
    }

    public List<FileEntry> execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();

        List<FileEntry> resultList = new ArrayList<>();

        Map<Pair, String> pairsWithFileDataToBeCommited = null;
        try {
            pairsWithFileDataToBeCommited = new DownloadRawData().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for(Pair pair : pairsWithFileDataToBeCommited.keySet()){
            String fileData = pairsWithFileDataToBeCommited.get(pair);
            String origin = pair.getOrigin();
            String destination = pair.getDestination();

            FileEntry commitFile = new FileEntry(new File(origin + "_" + destination), fileData, origin, destination);
            resultList.add(commitFile);
        }

        return resultList;

    }


    //tha pairnei orisma ena Pair object
    //opote an xreiastei tha kaneis polla commit File (mesa sti loopa tis execute)
    private String createUrl(Pair pair) throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(pair.getOrigin(), "utf-8");
        String urlDestination = URLEncoder.encode(pair.getDestination(), "utf-8");

        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY;
    }

    public class DownloadRawData extends AsyncTask<String, Void, Map<Pair, String>> {

        @Override
        public Map<Pair, String> doInBackground(String... params) {

            Map<Pair, String> results = new HashMap<>();

            for(Pair pair : nonExistingPairs){

                String link = null;

                try {
                    link = createUrl(pair);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    URL url = new URL(link);
                    InputStream is = url.openConnection().getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    results.put(pair, buffer.toString());
                    //return resultList;
                    is.close();
                    reader.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return results;

        }

        @Override
        protected void onPostExecute(Map<Pair, String> resultListWithoutCached) {
            try {
                Util.parseJSon(listener, resultListWithoutCached, pairCachedDestinations);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}