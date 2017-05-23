package com.yusuf.owtcast;

import android.media.AudioAttributes;
import android.widget.Toast;

import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;

import java.io.File;


import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;
/**
 * Created by DragonWarrior on 2/1/2017.
 */


public class Correlation {

    private static void Convert_MP3_To_Wav(String Input_MP3_File,String Output_Wav_File)
    {
        Converter converter = new Converter();
        try {
            converter.convert(Input_MP3_File, Output_Wav_File);
        }catch(Exception e) {
        }
    }

    //compare two mp3 files & calculate similarity using musicg library
    public static double getSimilarity(File firstFile, File secondFile)
    {
        String firstPath = firstFile.getAbsolutePath();
        String secondPath = secondFile.getAbsolutePath();
        String firstWavPath = firstPath.replace("mp3", "wav");
        String secondWavPath = secondPath.replace("mp3", "wav");

        Convert_MP3_To_Wav(firstPath, firstWavPath);
        Convert_MP3_To_Wav(secondPath, secondWavPath);
        Wave wave1 = new Wave(firstWavPath);;
        Wave wave2 = new Wave(secondWavPath);
        
        FingerprintSimilarity fps = wave1.getFingerprintSimilarity(wave2);
        double result = fps.getSimilarity();
        return result;
    }
}
