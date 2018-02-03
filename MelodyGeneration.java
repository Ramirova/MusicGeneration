import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class implements generation of melody based on chords using PSO Algorithm
 */
public class MelodyGeneration {
    public static int[] tonality;
    private final int NUMBER_OF_ITERATIONS = 1000;
    private final int NUMBER_OF_PARTICLES = 10000;
    public NoteSequence globalBest;
    public int countGood;
    public ChordSequence chordSequence;
    private int numberOfMadeIterations;
    private NoteSequence population[];
    private int bestFitness;

    /**
     * Constructor of class
     */
    MelodyGeneration() {
        tonality = new int[8];
        population = new NoteSequence[NUMBER_OF_PARTICLES];
        tonality = ChordSequenceGeneration.tonality;
        chordSequence = ChordSequenceGeneration.globalBest;
        bestFitness = 0;
        numberOfMadeIterations = 0;
        ChordSequence.length = 16;
    }

    /**
     * Method that generates Melody using PSO Algorithm
     */
    void generateMelody() {
        init();
        bestFitness = 0;
        countGood = 0;
        do {
            int minDif = 1000000;
            for (int i = 0; i < population.length; i++) { //Checking if new global best particle appeared

                if (bestFitness - fitness(population[i]) < minDif) minDif = bestFitness - fitness(population[i]);

                if (fitness(population[i]) > bestFitness) {


                    for (int j = 0; j < globalBest.length; j++) {
                        globalBest.notes[j] = population[i].notes[j]; //updating global best particle
                    }
                    bestFitness = fitness(population[i]); //updating global best
                }
            }
            for (int k = 0; k < population.length; k++) { //updating swarm
                population[k].updateVelocity();
                population[k].updateNotes();
            }
            numberOfMadeIterations++;
        } while (numberOfMadeIterations < NUMBER_OF_ITERATIONS);

        //Code for testing generated sequence
        int k = 0;
        for (int i = 0; i < globalBest.length; i++) {
            boolean result = true;
            if (!(globalBest.notes[i] <= 84 && globalBest.notes[i] >= 60)) result = false;
            if (i % 2 == 0) {
                int p = 0;
                if (globalBest.notes[i] % 12 == chordSequence.chords[k].notes[0] % 12) p = 1;
                if (globalBest.notes[i] % 12 == chordSequence.chords[k].notes[1] % 12) p = 1;
                if (globalBest.notes[i] % 12 == chordSequence.chords[k].notes[2] % 12) p = 1;
                k++;
                if (k == 8) k = 0;
                if (p == 0) result = false;
            } else {
                int p = 0;
                for (int t = 0; t < 8; t++) {
                    if (tonality[t] % 12 == globalBest.notes[i] % 12) p = 1;
                }
                if (p == 0) result = false;
            }
            if ((i > 0) && !(Math.abs(globalBest.notes[i] - globalBest.notes[i - 1]) <= 12)) result = false;
            if (result) countGood++;
        }
        if (!(globalBest.notes[globalBest.length - 1] % 12 == tonality[0] % 12)) countGood--;
    }

    /**
     * Method generates sequence of chords using PSO algorithm
     */
    private void init() {
        for (int i = 0; i < population.length; i++) {
            population[i] = new NoteSequence();
        }
        globalBest = new NoteSequence("q");
        System.arraycopy(population[0].notes, 0, globalBest.notes, 0, globalBest.length);
        bestFitness = fitness(globalBest);
    }


    /**
     * Fitness function for PSO Algorithm
     *
     * @param sequence that we need to assign fitness
     * @return fitness of given chords sequence
     */
    private int fitness(NoteSequence sequence) {
        int fitness = 0;
        int k = 0;
        for (int i = 0; i < sequence.length; i++) {
            //
            if (sequence.notes[i] <= 84 && sequence.notes[i] >= 60) fitness += 1000; //in range?
            if (i % 2 == 0) {
                if (sequence.notes[i] % 12 == chordSequence.chords[k].notes[0] % 12) fitness += 1000;
                if (sequence.notes[i] % 12 == chordSequence.chords[k].notes[1] % 12) fitness += 1000;
                if (sequence.notes[i] % 12 == chordSequence.chords[k].notes[2] % 12) fitness += 1000;
                k++;
                if (k == 8) k = 0;
            } else {
                for (int m = 0; m < 8; m++) {
                    if (tonality[m] % 12 == sequence.notes[i] % 12) fitness += 1000;
                }
            }
            if ((i > 0) && (Math.abs(sequence.notes[i] - sequence.notes[i - 1]) <= 12)) fitness += 1000;
        }

        if (sequence.notes[sequence.length - 1] % 12 == tonality[0] % 12) fitness += 1000;
        return fitness;
    }

    /**
     * Method return randomly generated double value in given range
     *
     * @param low  lower bound
     * @param high higher bound
     * @return randomly generated double value in given range
     */
    private double getRandDouble(int low, int high) {
        return ThreadLocalRandom.current().nextDouble(low, high + 1);
    }

    /**
     * Subclass implements sequence of notes (melody)
     */
    public class NoteSequence {
        int length;
        Random random;
        int notes[];
        double m;
        double c1;
        double c2;
        double vel[];
        NoteSequence myBest;

        NoteSequence(String s) {
            length = 16;
            notes = new int[length];
        }

        NoteSequence() {
            length = 16;
            vel = new double[length];
            notes = new int[length];
            random = new Random();
            for (int i = 0; i < length; i++) {
                notes[i] = random.nextInt(80) + 20;
                notes[i] = tonality[random.nextInt(7)] + 12 * random.nextInt(2) + 60;
                vel[i] = getRandDouble(-48, 48);
            }
            m = 0.2;
            c1 = 0.5;
            c2 = 1.1;
            myBest = new NoteSequence("q");
            for (int i = 0; i < myBest.length; i++) {
                myBest.notes[i] = notes[i];
            }
        }

        void updateVelocity() {
            for (int i = 0; i < vel.length; i++) {
                vel[i] = (vel[i] * m +
                        (c1 * random.nextInt(2) * (myBest.notes[i] - notes[i])) +
                        (c2 * random.nextInt(2) * (globalBest.notes[i] - notes[i])));
            }
        }

        void updateNotes() {
            for (int i = 0; i < notes.length; i++) {
                for (int j = 0; j < 3; j++) {
                    notes[i] += (int) vel[i];
                }
                while (notes[i] > 84 || notes[i] < 60) {
                    if (notes[i] > 84) {
                        notes[i] = notes[i] % 84;
                    }
                    if (notes[i] < 60) {
                        notes[i] = 60 + notes[i] % 60;
                    }
                }
            }
            if (fitness(this) > fitness(myBest)) {
                for (int i = 0; i < myBest.length; i++) {
                    myBest.notes[i] = notes[i];
                }
            }
        }
    }
}