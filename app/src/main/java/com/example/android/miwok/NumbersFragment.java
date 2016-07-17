package com.example.android.miwok;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NumbersFragment extends Fragment {


    // MediaPlayer object
    MediaPlayer mMediaPlayer;

    // AudioManager object
    private AudioManager mAudioManager;

    // AudioManager.OnAudioFocusChangeListener object
    private AudioManager.OnAudioFocusChangeListener mAfChangeListener;

    // MediaPlayer.OnCompletionListener object
    private MediaPlayer.OnCompletionListener mOnCompletionListener;

    public NumbersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.word_list, container, false);

        // Create a list of words
        final ArrayList<Word> words = new ArrayList<>();
        words.add(new Word("one", "lutti", R.drawable.number_one, R.raw.number_one));
        words.add(new Word("two", "otiiko", R.drawable.number_two, R.raw.number_two));
        words.add(new Word("three", "tolookosu", R.drawable.number_three, R.raw.number_three));
        words.add(new Word("four", "oyyisa", R.drawable.number_four, R.raw.number_four));
        words.add(new Word("five", "massokka", R.drawable.number_five, R.raw.number_five));
        words.add(new Word("six", "temmokka", R.drawable.number_six, R.raw.number_six));
        words.add(new Word("seven", "kenekaku", R.drawable.number_seven, R.raw.number_seven));
        words.add(new Word("eight", "kawinta", R.drawable.number_eight, R.raw.number_eight));
        words.add(new Word("nine", "wo’e", R.drawable.number_nine, R.raw.number_nine));
        words.add(new Word("ten", "na’aacha", R.drawable.number_ten, R.raw.number_ten));

        // Create an {@link WordAdapter}, whose data source is a list of {@link Word}s. The
        // adapter knows how to create list items for each item in the list.
        WordAdapter adapter = new WordAdapter(getActivity(), words, R.color.category_numbers);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml layout file.
        ListView listView = (ListView) rootView.findViewById(R.id.list);

        // Make the {@link ListView} use the {@link WordAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        listView.setAdapter(adapter);


        // listen for audio focus changes
        mAfChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    // pause playback when audio focus lost temporarily or even when "duck" is
                    // allowed because uninterrupted playback pronunciation is important
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        // pause and reset playback so that when audio focus is regained, audio
                        // file is played again.
                        mMediaPlayer.pause();
                        mMediaPlayer.seekTo(0);
                        break;

                    // stop playback when audio focus lost
                    case AudioManager.AUDIOFOCUS_LOSS:
                        // stop playback
                        mMediaPlayer.stop();
                        // release MediaPLayer object
                        releaseMediaPlayer();
                        break;

                    // resume playback when audio focus gained after losing
                    // temporarily
                    case AudioManager.AUDIOFOCUS_GAIN:
                        mMediaPlayer.start();
                        break;
                }
            }
        };

        // initialize MediaPlayer.OnCompletionListener
        mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // free MediaPlayer resources
                releaseMediaPlayer();
            }
        };

        // set click listener on list item view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // no need to get word at clicked position using adapterView
                // we can use position to locate the current word object in ArrayList
                final Word word = words.get(position);

                // free MediaPlayer resources
                releaseMediaPlayer();

                // initialize AudioManager
                mAudioManager = (AudioManager) getActivity()
                        .getSystemService(Context.AUDIO_SERVICE);

                // request audio focus for playback
                int result = mAudioManager.requestAudioFocus(mAfChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                // if audio focus granted, setup and start playback
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    // initialize and prepare MediaPlayer with audio file
                    mMediaPlayer = MediaPlayer.create(getActivity(), word.getAudioResourceId());

                    // start playback
                    mMediaPlayer.start();

                    // set a listener on MediaPlayer object to checked for completed state
                    mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

                }
            }
        });

        return rootView;
    }


    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            // lose audio focus when done playing file
            mAudioManager.abandonAudioFocus(mAfChangeListener);

        }
    }

    /**
     * To free resources on exiting the activity in case the audio file isn't completed.
     */
    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }
}
