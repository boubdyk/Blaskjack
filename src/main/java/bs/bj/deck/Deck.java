package bs.bj.deck;

import java.util.*;

/**
 * Created by boubdyk on 21.10.2015.
 */

public class Deck {

    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<Card>();
    }

    public void createDeck() {
        for (Suit cardSuit: Suit.values()) {
            for (Face cardFace : Face.values()) {
                this.cards.add(new Card(cardSuit, cardFace));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(this.cards, new Random());
    }

    public void removeCard(int index) {
        this.cards.remove(index);
    }

    public Card getCard(int index) {
        return this.cards.get(index);
    }

    public void addCard(Card addCard) {
        this.cards.add(addCard);
    }

    //Draws from the deck
    public void draw(Deck comingDeck) {
        this.cards.add(comingDeck.getCard(0));
        comingDeck.removeCard(0);
    }

    //Return total value of cards in deck
    public int cardsValue() {
        int totalValue = 0;
        int aces = 0;

        for (Card card: this.cards) {
            switch (card.getFace()) {
                case TWO: totalValue += 2; break;
                case THREE: totalValue += 3; break;
                case FOUR: totalValue += 4; break;
                case FIVE: totalValue += 5; break;
                case SIX: totalValue += 6; break;
                case SEVEN: totalValue += 7; break;
                case EIGHT: totalValue += 8; break;
                case NINE: totalValue += 9; break;
                case TEN: totalValue += 10; break;
                case JACK: totalValue += 10; break;
                case QUEEN: totalValue += 10; break;
                case KING: totalValue += 10; break;
                case ACE: aces += 1; break;
            }
        }

        for (int i = 0; i < aces; i++) {
            if (totalValue > 10) {
                totalValue += 1;
            } else {
                totalValue += 11;
            }
        }

        return totalValue;
    }

    public int deckSize() {
        return this.cards.size();
    }


    @Override
    public String toString() {
        String cardListOutput = "";
        for (Card card: this.cards) {
            cardListOutput += card.toString() + "\n";
        }
        return cardListOutput;
    }
}
