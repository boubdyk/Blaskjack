package bs.bj.entity;

import javax.persistence.*;

/**
 * Created by boubdyk on 30.10.2015.
 */

@Entity
@Table(name = "HISTORY")
public class EHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Column(name = "action_id")
    private Integer actionId;

    @Column(name = "player_id")
    private Integer playerId;

    @Column(name = "card_id")
    private Integer cardId;

    @Column(name = "bet")
    private Integer bet;

    @Column(name = "game_id")
    private Integer gameId;

    public EHistory(){}

    public EHistory(Integer actionId, Integer playerId, Integer gameId) {
        this.actionId = actionId;
        this.playerId = playerId;
        this.gameId = gameId;
    }

    public EHistory(Integer actionId, Integer playerId, Integer gameId, Integer bet) {
        this.actionId = actionId;
        this.playerId = playerId;
        this.gameId = gameId;
        this.bet = bet;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActionId() {
        return actionId;
    }

    public void setActionId(Integer actionId) {
        this.actionId = actionId;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getBet() {
        return bet;
    }

    public void setBet(Integer bet) {
        this.bet = bet;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

}
