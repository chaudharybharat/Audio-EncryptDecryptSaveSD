package com.example.saveaudiofilesd;

import android.graphics.Movie;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saveaudiofilesd.model.AudioModel;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownlaodSongActivity extends AppCompatActivity implements Handler.Callback {

    private List<AudioModel> audioModelList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AudioAdapter mAdapter;
    private Player player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downlaod_song);
        player = new Player(new Handler(this));
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        initComponet();
    }
    private void getAllfileThisFolder() {
        String path = FileUtils.getDirPath(this);
        Log.e("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.e("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            audioModelList.add(new AudioModel(files[i].getName(),directory+files[i].getName()));
            Log.e("Files", "FileName:" + files[i].getName());
        }
    }

    private void initComponet() {
        getAllfileThisFolder();
        mAdapter = new AudioAdapter(audioModelList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return false;
    }

    public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.MyViewHolder> {

        private List<AudioModel> audioModelList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_audio;

            public MyViewHolder(View view) {
                super(view);
                tv_audio = (TextView) view.findViewById(R.id.tv_audio);
            }
        }

        public AudioAdapter(List<AudioModel> audioModelList) {
            this.audioModelList = audioModelList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_audio_file, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final AudioModel audioModel = audioModelList.get(position);
            holder.tv_audio.setText(audioModel.getAudio());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        byte[] decrypt = decrypt(audioModel.getAudio());
                        if(decrypt!=null && decrypt.length>0){
                            playAudio(FileUtils.getTempFileDescriptor(DownlaodSongActivity.this,decrypt,audioModel.getAudio() ));
                        }
                    } catch (IOException e) {
                       // updateUI("Error Playing Audio.\nException: " + e.getMessage());
                        return;
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return audioModelList.size();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.destroyPlayer();
    }
    private byte[] decrypt(String file_name) {
        //updateUI("Decrypting file...");
        try {
            byte[] fileData = FileUtils.readFile(FileUtils.getFilePath(this,file_name));
            byte[] decryptedBytes = EncryptDecryptUtils.decode(EncryptDecryptUtils.getInstance(this).getSecretKey(), fileData);
            return decryptedBytes;
        } catch (Exception e) {
          //  updateUI("File Decryption failed.\nException: " + e.getMessage());
        }
        return null;
    }

    private void playAudio(FileDescriptor fileDescriptor) {
        if (null == fileDescriptor) {
            return;
        }
      //  updateUI("Playing audio...");
        player.play(fileDescriptor);
    }
}
