/**
 * Class that implements structure of chord
 */
class Chord {
    int notes[] = new int[3];

    Chord() {
        notes[0] = 0;
        notes[1] = 0;
        notes[2] = 0;
    }

    Chord(int firstNote, int secondNote, int thirdNote) {
        notes[0] = firstNote;
        notes[1] = secondNote;
        notes[2] = thirdNote;
    }
}