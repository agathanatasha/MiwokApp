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

public class FamilyFragment extends Fragment {
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

    public FamilyFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        Bundle savedInstanceState) {
        final ArrayList<Word> familyWords = new ArrayList<Word>(
            Arrays.asList(new Word("father", "әpә", R.drawable.family_father, R.raw.family_father),
                new Word("mother", "әṭa", R.drawable.family_mother, R.raw.family_mother),
                new Word("son", "angsi", R.drawable.family_son, R.raw.family_son),
                new Word("daughter", "tune", R.drawable.family_daughter, R.raw.family_daughter),
                new Word("older brother", "taachi", R.drawable.family_older_brother,
                    R.raw.family_older_brother),
                new Word("younger brother", "chalitti", R.drawable.family_younger_brother,
                    R.raw.family_younger_brother),
                new Word("older sister", "teṭe", R.drawable.family_older_sister,
                    R.raw.family_older_sister),
                new Word("younger sister", "kolliti", R.drawable.family_younger_sister,
                    R.raw.family_younger_sister),
                new Word("grandmother", "ama", R.drawable.family_grandmother,
                    R.raw.family_grandmother),
                new Word("grandfather", "paapa", R.drawable.family_grandfather,
                    R.raw.family_grandfather)));
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        WordAdapter itemsAdapter =
            new WordAdapter(getActivity(), familyWords, R.color.category_family);

        final ListView listView = (ListView) rootView.findViewById(R.id.list);

        listView.setAdapter(itemsAdapter);
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Word selectedWord = familyWords.get(i);
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