package bs.bj.entity;

import javax.persistence.*;

/**
 * Created by boubdyk on 01.11.2015.
 */

@Entity
@Table(name = "PLAYERSDECK")
public class EPlayersDeck {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Column(name = "card_id")
    private Integer cardId;

    @Column(name = "card_suit")
    private String cardSuit;

    @Column(name = "card_face")
    private String cardFace;

    @Column(name = "game_id")
    private Integer gameId;

    public EPlayersDeck() {}

    public EPlayersDeck(Integer cardId, String cardSuit, String cardFace, Integer gameId) {
        this.cardId = cardId;
        this.cardSuit = cardSuit;
        this.cardFace = cardFace;
        this.gameId = gameId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getCardSuit() {
        return cardSuit;
    }

    public void setCardSuit(String cardSuit) {
        this.cardSuit = cardSuit;
    }

    public String getCardFace() {
        return cardFace;
    }

    public void setCardFace(String cardFace) {
        this.cardFace = cardFace;
    }
}
