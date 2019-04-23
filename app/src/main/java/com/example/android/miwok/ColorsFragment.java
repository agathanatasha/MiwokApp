package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;

import static android.media.AudioManager.*;

public class ColorsFragment extends Fragment {
    private MediaPlayer mMediaPlayer;
    private OnCompletionListener pronunciationCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };

    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mAudioChangeListener =
        new OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int audioFocusState) {
                switch (audioFocusState) {
                    case (AUDIOFOCUS_GAIN):
                        mMediaPlayer.start();
                    case (AUDIOFOCUS_LOSS):
                        mMediaPlayer.stop();
                        releaseMediaPlayer();
                    case (AUDIOFOCUS_LOSS_TRANSIENT):
                        mMediaPlayer.pause();
                    case (AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                        mMediaPlayer.pause();
                }
            }
        };

    public ColorsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        final ArrayList<Word> colorWords = new ArrayList<Word>(
            Arrays.asList(new Word("red", "weṭeṭṭi", R.drawable.color_red, R.raw.color_red),
                new Word("green", "chokokki", R.drawable.color_green, R.raw.color_green),
                new Word("brown", "ṭakaakki", R.drawable.color_brown, R.raw.color_brown),
                new Word("gray", "ṭopoppi", R.drawable.color_gray, R.raw.color_gray),
                new Word("black", "kululli", R.drawable.color_black, R.raw.color_black),
                new Word("white", "kelelli", R.drawable.color_white, R.raw.color_white),
                new Word("dustry yellow", "ṭopiisә", R.drawable.color_dusty_yellow,
                    R.raw.color_dusty_yellow),
                new Word("mustard yellow", "chiwiiṭә", R.drawable.color_mustard_yellow,
                    R.raw.color_mustard_yellow)));

        View rootView = inflater.inflate(R.layout.word_list, container, false);

        WordAdapter itemsAdapter =
            new WordAdapter(getActivity(), colorWords, R.color.category_colors);

        final ListView listView = (ListView) rootView.findViewById(R.id.list);

        listView.setAdapter(itemsAdapter);
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Word selectedWord = colorWords.get(i);
                releaseMediaPlayer();

                int audioRequestResult =
                    mAudioManager.requestAudioFocus(mAudioChangeListener, STREAM_MUSIC,
                        AUDIOFOCUS_GAIN_TRANSIENT);

                if (audioRequestResult == AUDIOFOCUS_REQUEST_GRANTED) {
                    mMediaPlayer =
                        MediaPlayer.create(getActivity(), selectedWord.getPronunciation());
                    mMediaPlayer.start();
                    mMediaPlayer.setOnCompletionListener(pronunciationCompletionListener);
                } else {
                    Toast.makeText(getActivity(), "Audio request failed", Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioManager.abandonAudioFocus(mAudioChangeListener);
        }
    }
}
