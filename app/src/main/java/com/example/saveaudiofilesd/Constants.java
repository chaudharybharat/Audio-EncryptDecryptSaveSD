package com.example.saveaudiofilesd;

/**
 * Created by James From CoderzHeaven on 5/2/18.
 */

public class Constants {

    // Files
    //public static final String DOWNLOAD_AUDIO_URL = "http://www.noiseaddicts.com/samples_1w72b820/272.mp3";
   public static final String DOWNLOAD_AUDIO_URL = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
    public static final String FILE_NAME = extractFilename(DOWNLOAD_AUDIO_URL);
    public static final String TEMP_FILE_NAME = "temp";
    public static final String FILE_EXT = ".mp3";
    public static final String DIR_NAME = "Audio";
    public static final int OUTPUT_KEY_LENGTH = 256;

    // Algorithm
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String KEY_SPEC_ALGORITHM = "AES";
    public static final String PROVIDER = "BC";

    public static final String SECRET_KEY = "SECRET_KEY";
    public static String extractFilename(String urlDownloadLink){
        if(urlDownloadLink.equals("")){
            return "";
        }
        String newFilename = "";
        if(urlDownloadLink.contains("/")){
            int dotPosition = urlDownloadLink.lastIndexOf("/");
            newFilename = urlDownloadLink.substring(dotPosition + 1, urlDownloadLink.length());
        }
        else{
            newFilename = urlDownloadLink;
        }
        return newFilename;
    }

}

