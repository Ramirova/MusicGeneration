import java.util.Random;

/**
 * Class implements generation of chord sequence using PSO Algorithm
 */
class ChordSequenceGeneration {
    static int[] tonality;
    static ChordSequence globalBest;
    private static int tonalityType;
    private final int NUMBER_OF_ITERATIONS = 100;
    private final int NUMBER_OF_PARTICLES = 8000;
    int countGood;
    private ChordSequence population[];
    private int numberOfMadeIterations;
    private Random random;
    private int bestFitness;

    /**
     * Constructor of class
     */
    ChordSequenceGeneration() {
        tonality = new int[8];
        globalBest = new ChordSequence();
        population = new ChordSequence[NUMBER_OF_PARTICLES];
        random = new Random();
        countGood = 0;
        bestFitness = 0;
        numberOfMadeIterations = 0;
        selectTonality();
    }

    /**
     * Fitness function for PSO Algorithm
     *
     * @param sequence that we need to assign fitness
     * @return fitness of given chords sequence
     */
    static int fitness(ChordSequence sequence) {
        int fitness = 0;
        for (int i = 0; i < sequence.chords.length; i++) {
            //Conditions that check that chord lies in right interval
            if (sequence.chords[i].notes[0] <= 60 && sequence.chords[i].notes[0] >= 36) fitness += 5000;
            else fitness -= 2000;
            if (sequence.chords[i].notes[1] <= 60 && sequence.chords[i].notes[1] >= 36) fitness += 5000;
            else fitness -= 2000;
            if (sequence.chords[i].notes[2] <= 60 && sequence.chords[i].notes[2] >= 36) fitness += 5000;
            else fitness -= 2000;

            //Conditions that check that chord lies in right interval
            if (sequence.chords[i].notes[0] % 12 == tonality[0] % 12) { //tonic accords
                fitness += 5000;
                if (sequence.chords[i].notes[1] % 12 == (sequence.chords[i].notes[0] + 4) % 12 && tonalityType == 1) {
                    fitness += 5000;
                    if (sequence.chords[i].notes[2] % 12 == (sequence.chords[i].notes[1] + 3) % 12) {
                        fitness += 5000;
                    }
                }
                if (sequence.chords[i].notes[1] % 12 == (sequence.chords[i].notes[0] + 3) % 12 && tonalityType == 0) {
                    fitness += 5000;
                    if (sequence.chords[i].notes[2] % 12 == (sequence.chords[i].notes[1] + 4) % 12) {
                        fitness += 5000;
                    }
                }
            }
            if (sequence.chords[i].notes[0] % 12 == tonality[3] % 12) { //subdominant accords
                fitness += 5000;
                if (sequence.chords[i].notes[1] % 12 == (sequence.chords[i].notes[0] + 4) % 12 && tonalityType == 1) {
                    fitness += 5000;
                    if (sequence.chords[i].notes[2] % 12 == (sequence.chords[i].notes[1] + 3) % 12) {
                        fitness += 5000;
                    }
                }
                if (sequence.chords[i].notes[1] % 12 == (sequence.chords[i].notes[0] + 3) % 12 && tonalityType == 0) {
                    fitness += 5000;
                    if (sequence.chords[i].notes[2] % 12 == (sequence.chords[i].notes[1] + 4) % 12) {
                        fitness += 5000;
                    }
                }
            }
            if (sequence.chords[i].notes[0] % 12 == tonality[4] % 12) { //dominant accords
                fitness += 5000;
                if (sequence.chords[i].notes[1] % 12 == (sequence.chords[i].notes[0] + 4) % 12 && tonalityType == 1) {
                    fitness += 5000;
                    if (sequence.chords[i].notes[2] % 12 == (sequence.chords[i].notes[1] + 3) % 12) {
                        fitness += 5000;
                    }
                }
                if (sequence.chords[i].notes[1] % 12 == (sequence.chords[i].notes[0] + 3) % 12 && tonalityType == 0) {
                    fitness += 5000;
                    if (sequence.chords[i].notes[2] % 12 == (sequence.chords[i].notes[1] + 4) % 12) {
                        fitness += 5000;
                    }
                }
            }

            //Conditions that lowest notes of neighbour chord does not differ more that 12 chords
            int lowestNote;
            if (sequence.chords[i].notes[0] < sequence.chords[i].notes[1]) {
                lowestNote = sequence.chords[i].notes[0];
            } else lowestNote = sequence.chords[i].notes[1];
            if (lowestNote > sequence.chords[i].notes[2]) {
                lowestNote = sequence.chords[i].notes[2];
            }
            int lowestNotePrev = -1;
            if (i > 0) {
                if (sequence.chords[i - 1].notes[0] < sequence.chords[i].notes[1]) {
                    lowestNotePrev = sequence.chords[i - 1].notes[0];
                } else lowestNotePrev = sequence.chords[i - 1].notes[1];
                if (lowestNotePrev > sequence.chords[i - 1].notes[2]) {
                    lowestNotePrev = sequence.chords[i - 1].notes[2];
                }
            }
            if (i > 0 && Math.abs(lowestNote - lowestNotePrev) <= 12) fitness += 5000;
            //Conditions that check that current chord does not generate sequence of 4 equal chords
            if (i > 4) {
                if (sequence.chords[i].notes[0] != sequence.chords[i - 1].notes[0] &&
                        sequence.chords[i].notes[1] != sequence.chords[i - 1].notes[1] &&
                        sequence.chords[i].notes[2] != sequence.chords[i - 1].notes[2] &&

                        sequence.chords[i - 1].notes[0] != sequence.chords[i - 2].notes[0] &&
                        sequence.chords[i - 1].notes[1] != sequence.chords[i - 2].notes[1] &&
                        sequence.chords[i - 1].notes[2] != sequence.chords[i - 2].notes[2] &&

                        sequence.chords[i - 2].notes[0] != sequence.chords[i - 3].notes[0] &&
                        sequence.chords[i - 2].notes[1] != sequence.chords[i - 3].notes[1] &&
                        sequence.chords[i - 2].notes[2] != sequence.chords[i - 3].notes[2]) fitness += 5000;
            }
        }
        //Condition that checks that las note of sequence end to tonic note.
        if (sequence.chords[ChordSequence.length - 1].notes[0] % 12 == tonality[0] % 12) fitness += 5000;
        return fitness;
    }

    /**
     * Method that implements PSO for generating chords sequence
     */
    void generateSequence() {

        init();
        do {
            for (int i = 0; i < population.length; i++) { //Checking if new global best particle appeared
                if (fitness(population[i]) > bestFitness) {
                    for (int k = 0; k < ChordSequence.length; k++) {
                        for (int l = 0; l < 3; l++) {
                            globalBest.chords[k].notes[l] = population[i].chords[k].notes[l]; //updating best particle
                        }
                    }
                    bestFitness = fitness(population[i]); //updating best fitness
                }
            }
            for (int i = 0; i < population.length; i++) {
                population[i].updateVelocity();
                population[i].updateChords();
            }
            numberOfMadeIterations++;
        } while (numberOfMadeIterations < NUMBER_OF_ITERATIONS);

        //Code for testing generated sequence
        for (int i = 0; i < globalBest.chords.length; i++) {
            boolean result = true;
            if (!(globalBest.chords[i].notes[0] < 60 && globalBest.chords[i].notes[0] > 36)) result = false;
            if (!(globalBest.chords[i].notes[1] < 60 && globalBest.chords[i].notes[1] > 36)) result = false;
            if (!(globalBest.chords[i].notes[2] < 60 && globalBest.chords[i].notes[2] > 36)) result = false;
            if (!((globalBest.chords[i].notes[0] % 12 == tonality[0] % 12) ||
                    (globalBest.chords[i].notes[0] % 12 == tonality[3] % 12) ||
                    (globalBest.chords[i].notes[0] % 12 == tonality[4] % 12))) result = false;
            if (!((tonalityType == 1 && globalBest.chords[i].notes[1] % 12 == (globalBest.chords[i].notes[0] + 4) % 12 && globalBest.chords[i].notes[2] % 12 == (globalBest.chords[i].notes[1] + 3) % 12)
                    || (tonalityType == 0 && globalBest.chords[i].notes[1] % 12 == (globalBest.chords[i].notes[0] + 3) % 12 && globalBest.chords[i].notes[2] % 12 == (globalBest.chords[i].notes[1] + 4) % 12)))
                result = false;
            if (result) countGood++;
        }
        if (!(globalBest.chords[ChordSequence.length - 1].notes[0] % 12 == tonality[0] % 12)) countGood--;
    }

    /**
     * Method initializes population of chords with random notes and global best particle.
     */
    private void init() {
        for (int i = 0; i < population.length; i++) {
            population[i] = new ChordSequence();
        }
        globalBest = new ChordSequence("q");
        for (int k = 0; k < ChordSequence.length; k++) {
            for (int l = 0; l < 3; l++) {
                globalBest.chords[k].notes[l] = population[0].chords[k].notes[l];
            }
        }
    }

    /**
     * Method generates random tonality and initialize integer array attribute named tonality
     */
    private void selectTonality() {
        tonalityType = random.nextInt(2); //1 = major, 0 = minor;
        int tonNote = random.nextInt(13); //base note of tonality
        tonality[0] = tonNote;
        if (tonalityType == 1) { //majorTonalitySelected
            tonality[1] = tonality[0] + 2;
            tonality[2] = tonality[1] + 2;
            tonality[3] = tonality[2] + 1;
            tonality[4] = tonality[3] + 2;
            tonality[5] = tonality[4] + 2;
            tonality[6] = tonality[5] + 2;
            tonality[7] = tonality[6] + 1;
        } else { //minorTonalitySelected
            tonality[1] = tonality[0] + 2;
            tonality[2] = tonality[1] + 1;
            tonality[3] = tonality[2] + 2;
            tonality[4] = tonality[3] + 2;
            tonality[5] = tonality[4] + 1;
            tonality[6] = tonality[5] + 2;
            tonality[7] = tonality[6] + 2;
        }
    }
}
