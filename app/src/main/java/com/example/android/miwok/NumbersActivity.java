/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class NumbersActivity extends AppCompatActivity {

    // MediaPlayer object
    MediaPlayer mMediaPlayer;

    // AudioManager object
    private AudioManager mAudioManager;

    // AudioManager.OnAudioFocusChangeListener object
    private AudioManager.OnAudioFocusChangeListener mAfChangeListener;

    // MediaPlayer.OnCompletionListener object
    private MediaPlayer.OnCompletionListener mOnCompletionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

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
        WordAdapter adapter = new WordAdapter(this, words, R.color.category_numbers);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml layout file.
        ListView listView = (ListView) findViewById(R.id.list);

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
                mAudioManager = (AudioManager) NumbersActivity.this
                        .getSystemService(Context.AUDIO_SERVICE);

                // request audio focus for playback
                int result = mAudioManager.requestAudioFocus(mAfChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                // if audio focus granted, setup and start playback
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    // initialize and prepare MediaPlayer with audio file
                    mMediaPlayer = MediaPlayer.create(NumbersActivity.this,
                            word.getAudioResourceId());

                    // start playback
                    mMediaPlayer.start();

                    // set a listener on MediaPlayer object to checked for completed state
                    mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

                }
            }
        });

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
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }
}
