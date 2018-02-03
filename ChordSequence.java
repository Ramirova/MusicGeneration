import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class implements sequence of chords
 */
class ChordSequence {
    static int length;
    private Random random = new Random();
    Chord chords[] = new Chord[length];
    private double m ;
    private double c1;
    private double c2;
    private ChordSequence myBest;
    private double[][] vel = new double[length][3];

    ChordSequence(String s) {
        for (int i = 0; i < chords.length; i++) {
            chords[i] = new Chord();
        }
    }

    ChordSequence() {
        vel = new double[length][3];
        for (int i = 0; i < chords.length; i++) {
            int firstNote = random.nextInt(80);
            int secondNote = random.nextInt(80);
            int thirdNote = random.nextInt(80);
            chords[i] = new Chord(firstNote, secondNote, thirdNote);
            vel[i][0] = getRandDouble(-48, 48);
            vel[i][1] = getRandDouble(-48, 48);
            vel[i][2] = getRandDouble(-48, 48);
        }
        m = 0.1;
        c1 = 2;
        c2 = 1;
        myBest = new ChordSequence("C");
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < 3; j++) {
                myBest.chords[i].notes[j] = chords[i].notes[j];
            }
        }
    }

    void updateVelocity() {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < 3; j++) {
                vel[i][j] = (vel[i][j] * m +
                        (c1 * random.nextInt(2) * (myBest.chords[i].notes[j] - chords[i].notes[j])) +
                        (c2 * random.nextInt(2) * (ChordSequenceGeneration.globalBest.chords[i].notes[j] - chords[i].notes[j])));
            }
        }
    }

    void updateChords() {
        for (int i = 0; i < chords.length; i++) {
            for (int j = 0; j < 3; j++) {
                chords[i].notes[j] += (int) vel[i][j];
                while (chords[i].notes[j] > 60 || chords[i].notes[j] < 36) {
                    if (chords[i].notes[j] > 60) {
                        chords[i].notes[j] = chords[i].notes[j] % 60;
                    }
                    if (chords[i].notes[j] < 36) {
                        chords[i].notes[j] = 36 + chords[i].notes[j] % 60;
                    }
                }
            }
        }
        if (ChordSequenceGeneration.fitness(this) > ChordSequenceGeneration.fitness(myBest)) {
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < 3; j++) {
                    myBest.chords[i].notes[j] = chords[i].notes[j];
                }
            }
        }
    }

    /**
     * Method return randomly generated double value in given range
     * @param low lower bound
     * @param high higher bound
     * @return randomly generated double value in given range
     */
    private double getRandDouble(int low, int high) {
        return ThreadLocalRandom.current().nextDouble(low,high + 1);
    }
}