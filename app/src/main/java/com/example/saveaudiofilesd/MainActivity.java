package com.example.saveaudiofilesd;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.example.saveaudiofilesd.Constants.DOWNLOAD_AUDIO_URL;
import static com.example.saveaudiofilesd.Constants.FILE_NAME;


public class MainActivity extends AppCompatActivity implements OnDownloadListener, Handler.Callback {

    private Player player;
    private ProgressBar progressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = new Player(new Handler(this));
        progressbar = (ProgressBar)findViewById(R.id.progress_view);
    }

    public final void updateUI(String msg) {
        ((TextView) findViewById(R.id.statusTv)).setText(msg);
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.download:
                downloadAudio();
                break;
            case R.id.encrypt:
                if (encrypt()) updateUI("File encrypted successfully.");
                break;
            case R.id.decrypt:
                if (null != decrypt()) updateUI("File decrypted successfully.");
                break;
            case R.id.play:
                playClicked();
                break;
            default:
                updateUI("Unknown Click");
        }
    }

    private void playClicked() {
        try {
            byte[] decrypt = decrypt();
            if(decrypt!=null && decrypt.length>0){
                playAudio(FileUtils.getTempFileDescriptor(this,decrypt ));
            }
            getAllfileThisFolder();
        } catch (IOException e) {
            updateUI("Error Playing Audio.\nException: " + e.getMessage());
            return;
        }
    }

    private void getAllfileThisFolder() {
        String path = FileUtils.getDirPath(this);
        Log.e("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.e("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.e("Files", "FileName:" + files[i].getName());
        }
    }

    private void downloadAudio() {
        // Delete the old file //
       FileUtils.deleteDownloadedFile(this);
        updateUI("Downloading audio file...");
        Log.e("test","FileUtils.getDirPath(this)"+FileUtils.getDirPath(this));
        PRDownloader.download(DOWNLOAD_AUDIO_URL, FileUtils.getDirPath(this), FILE_NAME).build().start(this);


    /*  String  downloadAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File audioVoice = new File(downloadAudioPath + File.separator + "MyApp");
        if(!audioVoice.exists()){
            audioVoice.mkdir();
        }
        String filename = "song.mp3";
        downloadAudioPath = downloadAudioPath + File.separator + File.separator + filename;
        DownloadFile downloadAudioFile = new DownloadFile();

        downloadAudioFile.execute(DOWNLOAD_AUDIO_URL, downloadAudioPath);*/

    }

    /**
     * Encrypt and save to disk
     *
     * @return
     */
    private boolean encrypt() {
        updateUI("Encrypting file...");
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(this));
            byte[] encodedBytes = EncryptDecryptUtils.encode(EncryptDecryptUtils.getInstance(this).getSecretKey(), fileData);
            FileUtils.saveFile(encodedBytes, FileUtils.getFilePath(this));
            return true;
        } catch (Exception e) {
            updateUI("File Encryption failed.\nException: " + e.getMessage());
        }
        return false;
    }

    /**
     * Decrypt and return the decoded bytes
     *
     * @return
     */
    private byte[] decrypt() {
        updateUI("Decrypting file...");
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(this));
            byte[] decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(this).getSecretKey(), fileData);
            return decryptedBytes;
        } catch (Exception e) {
            updateUI("File Decryption failed.\nException: " + e.getMessage());
        }
        return null;
    }

    private void playAudio(FileDescriptor fileDescriptor) {
        if (null == fileDescriptor) {
            return;
        }
        updateUI("Playing audio...");
        player.play(fileDescriptor);
    }

    @Override
    public void onDownloadComplete() {
        updateUI("File Download complete");
    }

    @Override
    public void onError(Error error) {
        updateUI("File Download Error");
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.destroyPlayer();
    }

    @Override
    public boolean handleMessage(Message message) {
        if (null != message) {
            updateUI(message.obj.toString());
        }
        return false;
    }



    /*======================================================*/
    private class DownloadFile extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... url) {
            int count;
            try {
                URL urls = new URL(url[0]);
                URLConnection connection = urls.openConnection();
                connection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(urls.openStream());
                OutputStream output = new FileOutputStream(url[1]);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressbar.setVisibility(ProgressBar.GONE);
        }
    }
}
