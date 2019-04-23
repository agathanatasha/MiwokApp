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

public class NumbersFragment extends Fragment {

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

    public NumbersFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        final ArrayList<Word> numberWords = new ArrayList<Word>(
            Arrays.asList(new Word("one", "lutti", R.drawable.number_one, R.raw.number_one),
                new Word("two", "otiiko", R.drawable.number_two, R.raw.number_two),
                new Word("three", "tolookosu", R.drawable.number_three, R.raw.number_three),
                new Word("four", "oyyisa", R.drawable.number_four, R.raw.number_four),
                new Word("five", "massokka", R.drawable.number_five, R.raw.number_five),
                new Word("six", "temmokka", R.drawable.number_six, R.raw.number_six),
                new Word("seven", "kenekaku", R.drawable.number_seven, R.raw.number_seven),
                new Word("eight", "kawinta", R.drawable.number_eight, R.raw.number_eight),
                new Word("nine", "wo’e", R.drawable.number_nine, R.raw.number_nine),
                new Word("ten", "na’aacha", R.drawable.number_ten, R.raw.number_ten)));

        View rootView = inflater.inflate(R.layout.word_list, container, false);

        WordAdapter itemsAdapter =
            new WordAdapter(getActivity(), numberWords, R.color.category_numbers);

        final ListView listView = (ListView) rootView.findViewById(R.id.list);

        listView.setAdapter(itemsAdapter);
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Word selectedWord = numberWords.get(i);
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
