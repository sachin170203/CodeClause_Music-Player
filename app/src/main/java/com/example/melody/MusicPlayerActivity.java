package com.example.melody;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay, nextBtn, previousBtn, musicIcon;
    ArrayList<AudioModel>songList;
    AudioModel currentSong;
    int totalTime,position;
    MediaPlayer mediaPlayer=MyMediaPlayer.getInstance();
    private Animation animation;
    int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv =findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon=findViewById(R.id.music_icon_big);
        titleTv.setSelected(true);

        animation = AnimationUtils.loadAnimation(this,R.anim.slide_animation);
        titleTv.setAnimation(animation);
        songList= (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

//        setResourcesWithMusic();
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(mediaPlayer!=null){

               /*     seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                        musicIcon.setRotation(x++);
                        musicIcon.setColorFilter(Color.parseColor("FF0000"));
                    }else{
                        pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                        musicIcon.setRotation(0);
                    }       */
                    musicIcon.setRotation(x++);
                    int cP = mediaPlayer.getCurrentPosition();
                    Log.e("cp : ",String.valueOf(cP));
                    seekBar.setProgress(cP);

                    String elapsedTime = createTimeLabel(cP);
                    currentTimeTv.setText(elapsedTime);

                    String lastTime = createTimeLabel(totalTime);
                    totalTimeTv.setText(lastTime);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            mp.reset();

                            if (position == songList.size()-1)
                            {
                                position = 0;
                            }
                            else
                            {
                                position++;
                            }

                            String newPath = String.valueOf(songList.get(position));

                            try {
                                mp.setDataSource(newPath);
                                mp.prepare();
                                mp.start();
                                pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                                titleTv.clearAnimation();
                                titleTv.startAnimation(animation);


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            titleTv.setText(newPath.substring(newPath.lastIndexOf("/")+1));

                        }
                    });

                    new Handler().postDelayed(this,100);
                }

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    void setResourcesWithMusic(){
        currentSong=songList.get(MyMediaPlayer.currentIndex);
        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v->pausePlay());
        nextBtn.setOnClickListener(v->playNextSong());
        previousBtn.setOnClickListener(v->playPreviousSong());
        playMusic();
    }

    private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource (currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress (0);
            seekBar.setMax (mediaPlayer.getDuration());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void playNextSong (){
        if(MyMediaPlayer.currentIndex==songList.size()-1){
            return;
        }
        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic ();
    }
    private void playPreviousSong () {
        if(MyMediaPlayer.currentIndex==0){
            return;
        }
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithMusic ();
    }
    private void pausePlay(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        else{
            mediaPlayer.start();
        }
    }

    // passing the string as convertToMMSS.getCurrentPosition()
  public static String convertToMMSS (String duration){
        Long millis=Long.parseLong(duration);
        return  String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis)% TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis)% TimeUnit.MINUTES.toSeconds(millis));

    }

    public String createTimeLabel(int currentPosition)
    {
        String timeLabel;

        int minute = currentPosition / 1000 / 60;
        int second = currentPosition / 1000 % 60;

        if (second < 10)
        {
            timeLabel = minute+":0"+second;
        }
        else
        {
            timeLabel = minute + ":" + second;
        }

        return timeLabel;

    }
}