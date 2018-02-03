import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import java.io.File;
import java.io.IOException;


public class Main {
    static ChordSequenceGeneration chordSequenceGeneration;
    static MelodyGeneration melodyGeneration1;
    static MelodyGeneration melodyGeneration2;
    private static int melody[];

    public static void init() {
        chordSequenceGeneration = new ChordSequenceGeneration();
        melodyGeneration1 = new MelodyGeneration();
        melodyGeneration2 = new MelodyGeneration();
        melody = new int[32];
        ChordSequence.length = 8;
    }

    public static void main(String[] args) {
        init();
        chordSequenceGeneration.generateSequence();
        //finding ideal chord seqence
        while (chordSequenceGeneration.countGood != 8) {
            chordSequenceGeneration = new ChordSequenceGeneration();
            chordSequenceGeneration.generateSequence();
        }

        melodyGeneration1.chordSequence = ChordSequenceGeneration.globalBest;
        melodyGeneration2.chordSequence = ChordSequenceGeneration.globalBest;

        melodyGeneration1.generateMelody();
        //finding ideal first part melody
        while (melodyGeneration1.countGood != 16) {
            melodyGeneration1 = new MelodyGeneration();
            melodyGeneration1.generateMelody();
        }

        //finding ideal second part of melody
        melodyGeneration2.generateMelody();
        while (melodyGeneration2.countGood != 16) {
            melodyGeneration2 = new MelodyGeneration();
            melodyGeneration2.generateMelody();
        }
        int chords[] = new int[48];
        for (int i = 0; i < 24; i++) {
            chords[i] = chordSequenceGeneration.globalBest.chords[i / 3].notes[i % 3];
        }
        for (int i = 0; i < 24; i++) {
            chords[24+i] = chordSequenceGeneration.globalBest.chords[i / 3].notes[i % 3];
        }


        //preparing melody
        for (int i = 0; i < 16; i++) {
            melody[i] = melodyGeneration1.globalBest.notes[i];
        }
        for (int i = 16; i < 32; i++) {
            melody[i] = melodyGeneration2.globalBest.notes[i - 16];
        }

        createMidiFile(5, 200, chords, melody);
    }


    private static void createMidiFile(int recordNo, int tempo, int[] chords, int[] melody) {
        String musicString = "";
        String musicPath = "";
        int m = 0;
        int i = 0;
        for (int k = 0; k < 16; k++) {
            musicString = musicString + chords[i] + "h+";
            musicString = musicString + chords[i + 1] + "h+";
            musicString = musicString + chords[i + 2] + "h+";
            i += 3;
            musicString = musicString + melody[m] + "h ";
            musicString = musicString + melody[m + 1] + "h ";
            m += 2;
        }
        String midiFileNameEnd = ".mid";
        Pattern pattern = new Pattern(musicString).setVoice(0).setInstrument("Piano").setTempo(tempo);
        try {
            MidiFileManager.savePatternToMidi(pattern, new File(musicPath + Integer.toString(recordNo) + midiFileNameEnd));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}