package bs.bj.deck;

/**
 * Created by boubdyk on 21.10.2015.
 */

public class Card {
    private Suit suit;
    private Face face;



    public Card(Suit suit, Face face) {
        this.suit = suit;
        this.face = face;
    }

    @Override
    public String toString() {
        return this.suit.toString() + "-" + this.face.toString();
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public Face getFace() {
        return face;
    }

    public void setFace(Face face) {
        this.face = face;
    }
}
