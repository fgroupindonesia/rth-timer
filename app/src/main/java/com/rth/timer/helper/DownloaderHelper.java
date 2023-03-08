package com.rth.timer.helper;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.rth.timer.SettingsActivity;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class DownloaderHelper {

    Request request;
    Fetch fetch;
    Context context;
     ProgressBar progressBar;

    ImageButton buttonPlay;

    SettingsActivity setAct;

    public void close(){
        if(fetch!=null)
        fetch.close();
    }

    public void setPlayButton(ImageButton jb){
        buttonPlay = jb;
    }


    public void setProgressBar(ProgressBar prg){
        progressBar = prg;
    }

    public String getNameFromUrl(final String url) {
        String namina = Uri.parse(url).getLastPathSegment();
        if(!namina.contains(".mp3")){
            namina += ".mp3";
        }

        return  namina;
    }

    public  String getSaveDir() {

        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() ;

    }

    public DownloaderHelper(SettingsActivity app){
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(app)
                .setDownloadConcurrentLimit(3)
                .build();

        fetch = Fetch.Impl.getInstance(fetchConfiguration);

        context = app;
        setAct = app;
    }

    public boolean isFileDownloaded(String url){
            File f = new File(getSavedFilePath(url));

            return f.exists();
    }

    public String getSavedFilePath(String url){
        return getSaveDir() + "/" + getNameFromUrl(url);
    }

    public void download(String url){

        String savedFile = getSavedFilePath(url);

        request = new Request(url, savedFile);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);
        request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG");

        fetch.enqueue(request, updatedRequest -> {
            //Request was successfully enqueued for download.
        }, error -> {
            //An error occurred enqueuing the request.
        });

        fetch.addListener(fetchListener);

        //Remove listener when done.
        //fetch.removeListener(fetchListener);

    }

    FetchListener fetchListener = new FetchListener() {
        @Override
        public void onWaitingNetwork(@NotNull Download download) {

        }

        @Override
        public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {

        }

        @Override
        public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {

        }

        @Override
        public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

        }

        @Override
        public void onAdded(@NotNull Download download) {

        }

        @Override
        public void onQueued(@NotNull Download download, boolean waitingOnNetwork) {
            if (request.getId() == download.getId()) {
               // showDownloadInList(download);
            }
        }

        @Override
        public void onCompleted(@NotNull Download download) {
            progressBar.setVisibility(View.GONE);

            buttonPlay.setVisibility(View.VISIBLE);

            setAct.saveAudioMp3Config();
        }


        @Override
        public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
            if (request.getId() == download.getId()) {
               // updateDownload(download, etaInMilliSeconds);
            }
            int progress = download.getProgress();
            progressBar.setProgress(progress);
        }

        @Override
        public void onPaused(@NotNull Download download) {

        }

        @Override
        public void onResumed(@NotNull Download download) {

        }

        @Override
        public void onCancelled(@NotNull Download download) {

        }

        @Override
        public void onRemoved(@NotNull Download download) {

        }

        @Override
        public void onDeleted(@NotNull Download download) {

        }
    };

}
