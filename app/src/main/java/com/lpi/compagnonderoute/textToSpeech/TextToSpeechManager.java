package com.lpi.compagnonderoute.textToSpeech;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.lpi.compagnonderoute.R;
import com.lpi.compagnonderoute.utils.Preferences;
import com.lpi.reportlibrary.Report;

/**
 * Created by lucien on 24/04/2018.
 */

public class TextToSpeechManager
{
    public static final String MESSAGE = TextToSpeechManager.class.getName() + ".message";
    @Nullable
    private static TextToSpeechManager INSTANCE = null;
    private TextToSpeech tts;

    private Context _context;
//    private TextToSpeech _Tts;
//    private boolean _ttsInitialise = false;
//    @NonNull
//    private ArrayList<String> _pendingMessages = new ArrayList<>();
//    @NonNull
//    private static ArrayList<TextToSpeechListener> _listeners = new ArrayList<>();
//    private MediaPlayer _mediaPlayer;
//    private AudioManager _audioManager;

    /**
     * Point d'accès pour l'instance unique du singleton
     */
    @Nullable
    public static synchronized TextToSpeechManager getInstance(Context context)
    {
        if (INSTANCE == null)
        {
            INSTANCE = new TextToSpeechManager(context);
        }
        return INSTANCE;
    }

    /***
     * Constructeur
     * @param context
     */
    private TextToSpeechManager(Context context)
    {
        _context = context;

    }

    public void annonce(@NonNull final String message)
    {
        final Report r = Report.getInstance(_context);
        r.log(Report.NIVEAU.DEBUG, "annonce: " + message);

        try
        {
            // Emettre un beep puis parler
            final MediaPlayer mediaPlayer = MediaPlayer.create(_context, R.raw.beep2);
            final Preferences prefs = Preferences.getInstance(_context);
            final boolean changerVolume = !prefs.isVolumeDefaut();
            final float volume = (float) Preferences.getInstance(_context).getVolume() / (float) getMaxVolume();
            final boolean deconnecterBluetooth = isBluetoothConnected(_context) && prefs.isForcerHautParleur();

            /***
             * Quand le son sera fini
             */
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    r.log(Report.NIVEAU.DEBUG, "mediaplayer.OnCompletionListener");

                    // Le beep est termine, commencer a parler
                    tts = new TextToSpeech(_context, new TextToSpeech.OnInitListener()
                    {
                        /***
                         * TextToSpeech est initialisé
                         * @param status
                         */
                        @Override
                        public void onInit(int status)
                        {
                            r.log(Report.NIVEAU.DEBUG, "TextToSpeech.OnInitListener");
                            tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
                            {
                                @Override
                                public void onStart(String utteranceId)
                                {
                                    r.log(Report.NIVEAU.WARNING, "TextToSpeech.UtteranceProgressListener start" + utteranceId );
                                }

                                @Override
                                public void onDone(String utteranceId)
                                {
                                    r.log(Report.NIVEAU.WARNING, "TextToSpeech.UtteranceProgressListener done" + utteranceId );
                                    tts.shutdown();
                                }

                                @Override
                                public void onError(String utteranceId)
                                {
                                    r.log(Report.NIVEAU.WARNING, "TextToSpeech.UtteranceProgressListener error " + utteranceId );
                                }
                            });

                            if (status == TextToSpeech.SUCCESS && tts != null)
                            {
                                Bundle ttsParams = null;
                                if (changerVolume)
                                {
                                    ttsParams = new Bundle();
                                    ttsParams.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume);
                                }
                                tts.speak(message, TextToSpeech.QUEUE_ADD, ttsParams, this.getClass().getName());
                                if (deconnecterBluetooth)
                                    reconnectBluetooth(_context);

                            } else
                            {
                                r.log(Report.NIVEAU.WARNING, "TextToSpeech.OnInitListener");
                                r.log(Report.NIVEAU.WARNING, "Status =" + status);
                                r.log(Report.NIVEAU.WARNING, "tts = " + tts==null?"null" : tts.toString());
                            }
                            tts = null;
                        }
                    });
                    mediaPlayer.release();
                }
            });

            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
            // Attention: suite du code dans le OnCompletionListener (voir plus haut)
        } catch (Exception e)
        {
            r.log(Report.NIVEAU.ERROR, "Erreur lors de la creation du mediaplayer");
            r.log(Report.NIVEAU.ERROR, e);
            return;
        }
    }


    public int getMaxVolume()
    {
        return ((AudioManager) _context.getSystemService(Context.AUDIO_SERVICE)).getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
    }

    //
//    @Override
//    public void finalize()
//    {
//        flush();
//    }
//
//    public void flush()
//    {
//        if (_Tts != null)
//        {
//            _Tts.shutdown();
//            _Tts = null;
//        }
//
//        if (_mediaPlayer != null)
//        {
//            _mediaPlayer.release();
//            _mediaPlayer = null;
//        }
//    }
//
//    /***
//     * Constructeur
//     * @param context
//     */
//    private TextToSpeechManager(Context context)
//    {
//        _context = context;
//        _ttsInitialise = false;
//        try
//        {
//            _mediaPlayer = MediaPlayer.create(_context, R.raw.beep2);
//        } catch (Exception e)
//        {
//            _mediaPlayer = null;
//            e.printStackTrace();
//        }
//        try
//        {
//            _Tts = new TextToSpeech(context, this);
//        } catch (Exception e)
//        {
//            _Tts = null;
//            e.printStackTrace();
//        }
//        try
//        {
//            _audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        } catch (Exception e)
//        {
//            _audioManager = null;
//            e.printStackTrace();
//        }
//    }
//
//    /*******************************************************************************************************************
//     * Fait une annonce
//     * @param message
//     *******************************************************************************************************************/
//    public void annonce(String message)
//    {
//        if (_Tts == null)
//        {
//            _ttsInitialise = false;
//            _Tts = new TextToSpeech(_context, this);
//        }
//
//        if (!_ttsInitialise)
//        {
//            // TTS pas encore isInitialized, on memorise le message pour le dire quand l'initialisation sera terminee
//            _pendingMessages.add(message);
//        } else
//        {
//            Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
//            parleApresLeBip(message);
//        }
//    }
//
//    /*******************************************************************************************************************
//     * Emettre un bip (cloche, ping...) puis parler quand celui ci est termine
//     * @param message
//     *******************************************************************************************************************/
//    private void parleApresLeBip(final String message)
//    {
//        final Preferences prefs = Preferences.getInstance(_context);
//        final boolean changerVolume = !prefs.isVolumeDefaut();
//        final float volume = (float) Preferences.getInstance(_context).getVolume() / (float) getMaxVolume();
//        final boolean deconnecterBluetooth = isBluetoothConnected(_context) && prefs.isForcerHautParleur();
//        try
//        {
//            if (_mediaPlayer == null)
//            {
//                // Parler sans le beep prealable
//                final Bundle ttsParams = new Bundle();
//                if (changerVolume)
//                    ttsParams.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume);
//                if (deconnecterBluetooth)
//                    deconnectBluetooth(_context);
//                _Tts.speak(message, TextToSpeech.QUEUE_ADD, ttsParams, null);
//
//                if (deconnecterBluetooth)
//                    reconnectBluetooth(_context);
//            } else
//            {
//                if (deconnecterBluetooth)
//                    deconnectBluetooth(_context);
//
//                // Emettre un beep puis parler
//                _mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
//                {
//                    @Override
//                    public void onCompletion(MediaPlayer mp)
//                    {
//                        // Le beep est termine, commencer a parler
//                        final Bundle ttsParams = new Bundle();
//                        if (changerVolume)
//                            ttsParams.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume);
//                        _Tts.speak(message, TextToSpeech.QUEUE_ADD, ttsParams, null);
//
//                        if (deconnecterBluetooth)
//                            reconnectBluetooth(_context);
//                    }
//                });
//
//                _mediaPlayer.setVolume(volume, volume);
//                _mediaPlayer.seekTo(0);
//                _mediaPlayer.start();
//                // Attention: suite du code dans le OnCompletionListener (voir plus haut)
//            }
//
//        } catch (IllegalStateException e)
//        {
//            _Tts.shutdown();
//            _Tts = null;
//            _ttsInitialise = false;
//            e.printStackTrace();
//        }
//    }
//
    private void reconnectBluetooth(Context context)
    {
        // TODO: reconnecter bluetooth
        // _audioManager.setMode(0);
        // _audioManager.setBluetoothScoOn(true);
        // _audioManager.startBluetoothSco();
    }

    /*******************************************************************************************************************
     * Deconnecte temporairement le bluetooth
     * @param context
     */
    private void deconnectBluetooth(Context context)
    {
        // TODO: deconnecter bluetooth
        //_audioManager.setBluetoothScoOn(false);
        //_audioManager.stopBluetoothSco();
        //_audioManager.setMode(AudioManager.MODE_NORMAL);
    }

    /*******************************************************************************************************************
     * Retourne vrai si un ecouteur bluetooth est connecte
     * @param context
     * @return
     */
    private boolean isBluetoothConnected(Context context)
    {
        // TODO: is bluetooth connected
        return false;
        //BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //return (bluetoothAdapter != null && BluetoothProfile.STATE_CONNECTED == bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET));
    }

    //    private void parle(String message)
//    {
//        Preferences preferences = Preferences.getInstance(_context);
//        final Bundle params = new Bundle();
//        if (!preferences.isVolumeDefaut())
//        {
//            // Changement de volume
//            float v = Preferences.getInstance(_context).getVolume();
//            float max = getMaxVolume();
//            String volume = "" + (v / max);
//
//            params.putString(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume);
//        }
//
//    }
//
    public void annonce(@NonNull Context ctx, int resId, Object... args)
    {
        String format = ctx.getResources().getString(resId);
        annonce(String.format(format, args));
    }
//
//    /**
//     * Called to signal the completion of the TextToSpeech engine initialization.
//     *
//     * @param status {@link TextToSpeech#SUCCESS} or {@link TextToSpeech#ERROR}.
//     */
//    @Override
//    public void onInit(int status)
//    {
//        switch (status)
//        {
//            case TextToSpeech.ERROR:
//                break;
//
//            case TextToSpeech.SUCCESS:
//                _ttsInitialise = true;
//                // Dire les message qu'on avait memorise avant l'initialisation du TTS
//                if (_pendingMessages.size() > 0)
//                {
//                    StringBuilder sb = new StringBuilder();
//                    for (String m : _pendingMessages)
//                        sb.append(m).append("\n");
//                    _pendingMessages.clear();
//                    parleApresLeBip(sb.toString());
//                }
//                break;
//        }
//
//        for (TextToSpeechListener listener : _listeners)
//            listener.onTTSInit(status);
//    }
//
////	/**
////	 * Called when an utterance "starts" as perceived by the caller. This will
////	 * be soon before audio is played back in the case of a {@link TextToSpeech#speak}
////	 * or before the first bytes of a file are written to the file system in the case
////	 * of {@link TextToSpeech#synthesizeToFile}.
////	 *
////	 * @param utteranceId The utterance ID of the utterance.
////	 */
////	@Override
////	public void onStart(String utteranceId)
////	{
////	}
////
////	/**
////	 * Called when an utterance has successfully completed processing.
////	 * All audio will have been played back by this point for audible output, and all
////	 * output will have been written to disk for file synthesis requests.
////	 * <p>
////	 * This request is guaranteed to be called after {@link #onStart(String)}.
////	 *
////	 * @param utteranceId The utterance ID of the utterance.
////	 */
////	@Override
////	public void onDone(String utteranceId)
////	{
////	}
////
////	/**
////	 * Called when an error has occurred during processing. This can be called
////	 * at any point in the synthesis process. Note that there might be calls
////	 * to {@link #onStart(String)} for specified utteranceId but there will never
////	 * be a call to both {@link #onDone(String)} and {@link #onError(String)} for
////	 * the same utterance.
////	 *
////	 * @param utteranceId The utterance ID of the utterance.
////	 * @deprecated Use {@link #onError(String, int)} instead
////	 */
////	@Override
////	public void onError(String utteranceId)
////	{
////
////	}
//
//    public void addListener(TextToSpeechListener listener)
//    {
//        for (TextToSpeechListener l : _listeners)
//            if (listener == l)
//                // Deja present dans la liste
//                return;
//
//        _listeners.add(listener);
//    }
//
//    public void removeListener(TextToSpeechListener listener)
//    {
//        _listeners.remove(listener);
//    }
//
//    /***
//     * retourne vrai si ce manager est initialise
//     * @return
//     */
//    public boolean isInitialized()
//    {
//        return _ttsInitialise;
//    }
//

    /***
     * Fait une annonce depuis un BroadcastReceiver
     * Sous Android, un broadcastReceiver ne peut pas creer un service (TextToSpeech)
     * https://stackoverflow.com/questions/4497757/android-call-tts-in-broadcastreceiver
     * @param message
     */
    public void annonceFromReceiver(final String message)
    {
        try
        {
            Intent speechIntent = new Intent();
            speechIntent.setClass(_context, TextToSpeechActivity.class);
            speechIntent.putExtra(MESSAGE, message);
            speechIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(speechIntent);
        } catch (Exception e)
        {
            Report r = Report.getInstance(_context);
            r.log(Report.NIVEAU.ERROR, "TextToSpeechManager.annonceFromReceiver");
            r.log(Report.NIVEAU.ERROR, e);
        }
    }
}
