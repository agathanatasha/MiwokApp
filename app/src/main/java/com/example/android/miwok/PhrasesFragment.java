package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
import static android.media.AudioManager.STREAM_MUSIC;

public class PhrasesFragment extends Fragment {
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

    public PhrasesFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        final ArrayList<Word> phrases =
            new ArrayList<Word>(Arrays.asList(new Word("Where are you going?", "minto wuksus", R.raw.phrase_where_are_you_going),
                new Word("What is your name?", "tinnә oyaase'nә", R.raw.phrase_what_is_your_name),
                new Word("My name is ...", "oyaaset...", R.raw.phrase_my_name_is),
                new Word("How are you feeling?","michәksәs?", R.raw.phrase_how_are_you_feeling),
                new Word("I’m feeling good.", "kuchi achit", R.raw.phrase_im_feeling_good)
            ));
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        WordAdapter itemsAdapter =
            new WordAdapter(getActivity(), phrases, R.color.category_phrases);

        final ListView listView = (ListView) rootView.findViewById(R.id.list);

        listView.setAdapter(itemsAdapter);
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Word selectedWord = phrases.get(i);
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
