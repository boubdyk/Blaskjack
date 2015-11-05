package bs.bj.service;

import bs.bj.dao.*;
import bs.bj.entity.*;
import bs.bj.deck.Card;
import bs.bj.deck.Deck;
import bs.bj.deck.Face;
import bs.bj.deck.Suit;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by boubdyk on 31.10.2015.
 */

@Named
@Transactional
public class GameService {

    @Inject
    private GameDAO gameDAO;

    @Inject
    private PlayerDAO playerDAO;

    @Inject
    private PlayingDeckService playingDeckService;

    @Inject
    private PlayersDeckDAO playersDeckDAO;

    @Inject
    private DealersDeckDAO dealersDeckDAO;

    @Inject
    private HistoryService historyService;

    @Inject
    private ActionDAO actionDAO;

    @Inject
    private HistoryDAO historyDAO;

    @Inject
    private PlayerService playerService;

    public GameService() {}

    /**
     * Method checks if input data is valid.
     * @param gameId game round identifier.
     * @return true if data valid or false if doesn't.
     */
    boolean isValidID(Integer gameId) {
        return (gameId == null || gameDAO.read(gameId) == null) ? false : true;
    }

    /**
     * Method invokes when user is not registered in DB. Set players balance and return user id.
     * @param balance players balance.
     * @return players unique identifier.
     */
    public Integer registerPlayer(Integer balance) {
        if (balance == null || balance.intValue() <= 0) return null;
        EPlayer newPlayer = new EPlayer(balance);
        return playerDAO.create(newPlayer);
    }

    /**
     * Invokes when user add balance. Return new balance.
     * @param playerId
     * @param balance
     * @return new balance.
     */
    public Integer addBalance(Integer playerId, Integer balance) {
        EPlayer player = playerDAO.read(playerId);
        Integer newBalance = player.getBalance() + balance;
        player.setBalance(newBalance);
        playerDAO.update(player);
        historyService.addHistory(playerId, null, actionDAO.getID(ActionEnum.MODIFY_BALANCE.toString()));
        return player.getBalance();
    }

    /**
     * Invokes when player make a bet. Return true if bet is valid else return false. Returns new balance.
     * @param gameId game round identifier.
     * @param playerID player identifier.
     * @param bet money that player wants to bet.
     * @return new players balance (players balance - bet).
     */
    public Integer makeBet(Integer gameId, Integer playerID, Integer bet) {
        if (playerID == null || bet == null || bet <= 0) return null;
        EPlayer ePlayer = playerDAO.read(playerID);
        if (ePlayer == null || ePlayer.getBalance() < bet) return null;
        ePlayer.setBalance(ePlayer.getBalance() - bet);
        playerDAO.update(ePlayer);
        EHistory eHistory = new EHistory();
        eHistory.setPlayerId(playerID);
        eHistory.setBet(bet);
        eHistory.setActionId(actionDAO.getID(ActionEnum.BET.toString()));
        eHistory.setGameId(gameId);
        historyDAO.create(eHistory);
        return ePlayer.getBalance();
    }

    /**
     * Invokes when new game round start. Return gameId.
     * @param playerId player identifier.
     * @return game identifier.
     */
    public Integer onGameStart(Integer playerId) {
        if (playerId == null) return null;
        EPlayer ePlayer = playerDAO.read(playerId);
        if (ePlayer == null) return null;
        EGame newGame = new EGame();
        newGame.setPlayerId(ePlayer.getId());
        newGame.setDateStart(new Date());
        Integer gameId = gameDAO.create(newGame);
        playingDeckService.createDeck(gameId);
        return gameId;
    }

    /**
     * Draw first two cards for player and dealer. Set cards values.
     * @param gameId game identifier.
     * @return Map of players cards as key and dealers cards as values.
     */
    public Map<EPlayersDeck, EDealersDeck> drawCards(Integer gameId) {
        Map<EPlayersDeck, EDealersDeck> resultMap = new HashMap<EPlayersDeck, EDealersDeck>();
        resultMap.put(playingDeckService.drawCardForPlayer(gameId), playingDeckService.drawCardForDealer(gameId));
        resultMap.put(playingDeckService.drawCardForPlayer(gameId), playingDeckService.drawCardForDealer(gameId));
        updateGame(gameId);
        return resultMap;
    }

    /**
     * This method is used to update 2 fields in table Game: playersScore and dealersScore.
     * @param gameId game round identifier.
     * @return Map of players score as key and dealers score as value.
     */
    public Map<Integer, Integer> updateGame(Integer gameId) {
        EGame eGame = gameDAO.read(gameId);
        eGame.setPlayerScore(playersDeckScore(gameId));
        eGame.setDealerScore(dealersDeckScore(gameId));
        gameDAO.update(eGame);
        return new HashMap<Integer, Integer>(eGame.getPlayerScore(), eGame.getDealerScore());
    }


    /**
     * If player Hits than draw card for him. Return Map of card and total cards value.
     * @param gameId
     * @param playerId
     * @return Map of drawn card as key and total score of players deck as value.
     */
    public Map<EPlayersDeck, Integer> drawCardForPlayer(Integer gameId, Integer playerId) {
        if (gameId == null || gameDAO.read(gameId) == null) return null;
        EPlayersDeck ePlayersDeck = playingDeckService.drawCardForPlayer(gameId);
        Map<EPlayersDeck, Integer> resultMap = new HashMap<EPlayersDeck, Integer>();
        resultMap.put(ePlayersDeck, playersDeckScore(gameId));
        historyService.addHistory(playerId, gameId, actionDAO.getID(ActionEnum.HIT.toString()));
        return resultMap;
    }


    /**
     * If player stands than dealer draws card for himself. Return Map of card and total cards value.
     * @param gameId game round identifier.
     * @param playerId
     * @return Map of dealers drawn card as key and total dealers deck score as value.
     */
    public Map<EDealersDeck, Integer> drawCardForDealer(Integer gameId, Integer playerId) {
        if (!isValidID(gameId)) return null;
        EDealersDeck eDealersDeck = playingDeckService.drawCardForDealer(gameId);
        Map<EDealersDeck, Integer> resultMap = new HashMap<EDealersDeck, Integer>();
        resultMap.put(eDealersDeck, dealersDeckScore(gameId));
        historyService.addHistory(playerId, gameId, actionDAO.getID(ActionEnum.STAND.toString()));
        return resultMap;
    }


    /**
     * Used to get players deck score in current game round.
     * @param gameId game round identifier.
     * @return deck score.
     */
    public Integer playersDeckScore(Integer gameId) {
        Deck playersDeck = new Deck();
        Card card;
        for (EPlayersDeck playerCards: playersDeckDAO.getCards(gameId)) {
            card = new Card(Suit.valueOf(playerCards.getCardSuit()), Face.valueOf(playerCards.getCardFace()));
            playersDeck.addCard(card);
        }
        return playersDeck.cardsValue();
    }

    /**
     * Used to get dealers deck score in current game round.
     * @param gameId game round identifier.
     * @return deck score.
     */
    public Integer dealersDeckScore(Integer gameId) {
        Deck dealersDeck = new Deck();
        Card card;
        for (EDealersDeck dealersCards: dealersDeckDAO.getCards(gameId)) {
            card = new Card(Suit.valueOf(dealersCards.getCardSuit()), Face.valueOf(dealersCards.getCardFace()));
            dealersDeck.addCard(card);
        }
        return dealersDeck.cardsValue();
    }

    /**
     * Invokes in method gameRoundResult to set data(new balance, action) to DB if player wins.
     * @param gameId game round identifier.
     * @param playerId players identifier.
     * @param bet players bet for current game round.
     * @param isBlackjack boolean value. Takes true only after dealer draw first two cards for himself and
     *                    for player.
     * @return action. ("WIN")
     */
    private String playerWin(Integer gameId, Integer playerId, Integer bet, boolean isBlackjack) {
        String result = ActionEnum.WIN.toString();
        historyService.addHistory(playerId, gameId, actionDAO.getID(result));
        EGame eGame = gameDAO.read(gameId);
        int price = isBlackjack ? bet + bet * 3 / 2 : bet * 2;
        eGame.setPrice(price);
        addBalance(playerId, price);
        eGame.setWinner("PLAYER");
        eGame.setDateFinish(new Date());
        gameDAO.update(eGame);
        return result;
    }

    /**
     * Invokes to put data to DB if player lose.
     * @param gameId game round identifier.
     * @param playerId player identifier.
     * @return action. ("BUSTED")
     */
    public String playerBusted(Integer gameId, Integer playerId) {
        String result = ActionEnum.BUSTED.toString();
        historyService.addHistory(playerId, gameId, actionDAO.getID(result));
        EGame eGame = gameDAO.read(gameId);
        eGame.setPrice(0);
        eGame.setWinner("DEALER");
        eGame.setDateFinish(new Date());
        gameDAO.update(eGame);
        return result;
    }

    /**
     * Invokes to set data to DB if dealer has the same score as player.
     * @param gameId game roung identifier.
     * @param playerId player identifier.
     * @param bet money that player bet.
     * @return action. ("PUSH")
     */
    private String roundPush(Integer gameId, Integer playerId, Integer bet) {
        String result = ActionEnum.PUSH.toString();
        historyService.addHistory(playerId, gameId, actionDAO.getID(result));
        EGame eGame = gameDAO.read(gameId);
        eGame.setPrice(bet);
        addBalance(playerId, bet);
        eGame.setWinner("PUSH");
        eGame.setDateFinish(new Date());
        gameDAO.update(eGame);
        return result;
    }

    /**
     * Used to get result of current game round. Returns the winner.
     * @param gameId game ound identifier.
     * @param playerId player identifier.
     * @param bet bet.
     * @param isBlackjack boolean value. Takes true only after dealer draw first two cards for himself and
     *                    for player.
     * @return winner. ("PLAYER", "DEALER", "PUSH")
     */
    public String gameRoundResult(Integer gameId, Integer playerId, Integer bet, boolean isBlackjack) {
        if (dealersDeckScore(gameId) > 21) return playerWin(gameId, playerId, bet, isBlackjack);
        if (playersDeckScore(gameId) > 21) return playerBusted(gameId, playerId);
        if (dealersDeckScore(gameId) > playersDeckScore(gameId)) return playerBusted(gameId, playerId);
        if (dealersDeckScore(gameId) < playersDeckScore(gameId)) return playerWin(gameId, playerId, bet, isBlackjack);
        return roundPush(gameId, playerId, bet);

    }

    /**
     * Used to check if game round has been finished.
     * @param gameId game round identifier.
     * @return true if round has been finished and false if it doesn't
     */
    public boolean isRoundFinished(Integer gameId) {
        if (gameId == null || gameDAO.read(gameId) == null) return true;
        return gameDAO.getWinner(gameId) == null ? false : true;
    }

    /**
     * Used to get players bet for current game round. Method doesn't check input values because it invokes
     * from place where all checking has been made.
     * @param gameId game round identifier.
     * @param playerId players identifier.
     * @return bet.
     */
    public Integer getRoundsBet(Integer gameId, Integer playerId) {
        return historyDAO.getBet(gameId, playerId, actionDAO.getID("BET"));
    }

    /**
     * Used to get players current balance.
     * @param playerId player identifier.
     * @return players balance.
     */
    public Integer getPlayersBalance(Integer playerId) {
        return playerService.getBalance(playerId);
    }

}
