package bs.bj.web;

import bs.bj.entity.EDealersDeck;
import bs.bj.entity.EPlayersDeck;
import bs.bj.service.GameService;
import bs.bj.service.Helper;
import bs.bj.service.HistoryService;
import bs.bj.service.PlayerService;
import org.json.simple.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by boubdyk on 03.11.2015.
 */

@Path("/blackjack")
public class GameRestService {

    private static ApplicationContext context;
    private static GameService gameService;
    private static HistoryService historyService;
    private static Helper helper;
    private static PlayerService playerService;

    private final int MIN_BET = 5;

    static {
        context = new ClassPathXmlApplicationContext("META-INF/beans.xml");
        gameService = context.getBean(GameService.class);
        historyService = context.getBean(HistoryService.class);
        helper = context.getBean(Helper.class);
        playerService = context.getBean(PlayerService.class);
    }


    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public final Response registerPlayer(final String input) {
        JSONObject inputObject = helper.parse(input);

        Long balance = (Long)inputObject.get("balance");
        if (balance <= 0) return Response.status(Constants.CODE_NOT_MODIFIED).build();

        Integer playerId = gameService.registerPlayer(balance.intValue());

        if (playerId == null) {
            return Response.status(Constants.CODE_NOT_MODIFIED).build();
        } else {
            JSONObject returnObject = new JSONObject();
            returnObject.put("playerId", playerId);
            return Response.status(Constants.CODE_CREATED).entity(returnObject).build();
        }
    }


    @POST
    @Path("/bet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public final Response makeBet(final String input) {
        JSONObject inputObject = helper.parse(input);
        Integer gameId = ((Long)inputObject.get("gameId")).intValue();
        Integer playerId = ((Long)inputObject.get("playerId")).intValue();
        Integer bet = ((Long)inputObject.get("bet")).intValue();

        Integer newBalance = gameService.makeBet(gameId, playerId, bet);


        if (newBalance == null || newBalance < 0) {
            return Response.status(Constants.CODE_NOT_MODIFIED).build();
        } else {
            Map<EPlayersDeck, EDealersDeck> cardsMap = gameService.drawCards(gameId);

            Integer playersDeckscore = gameService.playersDeckScore(gameId);
            Integer dealersDeckScore = gameService.dealersDeckScore(gameId);

            //Checking if there's Blackjack on the table!!!
            boolean isBlackjack = (playersDeckscore == 21 || dealersDeckScore == 21) ? true : false;
            String gameRoundResult = isBlackjack ? gameService.gameRoundResult(gameId, playerId, bet, isBlackjack) : null;

            boolean isRoundFinished = gameRoundResult == null ? false : true;

            if (!isRoundFinished) {
                gameRoundResult = "HIT/STAND";
            }

            JSONObject returnObject = new JSONObject();
            returnObject.put("newBalance", newBalance);
            returnObject.put("cards", helper.parseCardsToJSON(cardsMap));
            returnObject.put("playersDeckScore", playersDeckscore);
            returnObject.put("dealersDeckScore", dealersDeckScore);
            returnObject.put("gameRoundResult", gameRoundResult);
            returnObject.put("isRoundFinished", isRoundFinished);
            return Response.status(Constants.CODE_CREATED).entity(returnObject).build();
        }
    }

    @GET
    @Path("/hit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response playerHits(@QueryParam("gameId") final Long game,
                                 @QueryParam("playerId") final Long player) {
        Integer gameId = game.intValue();
        Integer playerId = player.intValue();
        if (gameService.isRoundFinished(gameId)) {
            return Response.status(Constants.CODE_NOT_FOUND).build();
        }
        Map<EPlayersDeck, Integer> playersCardMap = new HashMap<EPlayersDeck, Integer>();
        Map<EPlayersDeck, Integer> tmpMap = gameService.drawCardForPlayer(gameId, playerId);
        if (tmpMap == null) {
            return Response.status(Constants.CODE_NOT_MODIFIED).build();
        }
        playersCardMap.put(tmpMap.keySet().iterator().next(), tmpMap.values().iterator().next());
        //Update game state to recount players score.
        gameService.updateGame(gameId);

        String gameRoundResult;
        boolean isRoundFinished;

        Integer playersDeckScore = playersCardMap.values().iterator().next();

        if (playersDeckScore > 21) {
            gameRoundResult = gameService.playerBusted(gameId, playerId);
            isRoundFinished = true;
        } else {
            gameRoundResult = "HIT/STAND";
            isRoundFinished = false;
        }

        JSONObject returnObject = new JSONObject();
        returnObject.put("drawnCard", helper.parseDrawnCardToJSON(playersCardMap.keySet().iterator().next()));
        returnObject.put("playersDeckScore", playersDeckScore);
        returnObject.put("isRoundFinished", isRoundFinished);
        returnObject.put("gameRoundResult", gameRoundResult);
        returnObject.put("newBalance", gameService.getPlayersBalance(playerId));
        return Response.status(Constants.CODE_OK).entity(returnObject).build();
    }

    @GET
    @Path("/stand")
    @Produces(MediaType.APPLICATION_JSON)
    public Response playerStands(@QueryParam("gameId") final Long game,
                                   @QueryParam("playerId") final Long player) {
        Integer gameId = game.intValue();
        Integer playerId = player.intValue();
        if (gameService.isRoundFinished(gameId)) {
            return Response.status(Constants.CODE_NOT_FOUND).build();
        }

        Map<EDealersDeck, Integer> dealersCardMap = new HashMap<EDealersDeck, Integer>();
        Map<EDealersDeck, Integer> tmpMap;
        while (gameService.dealersDeckScore(gameId) <= 17) {
            tmpMap = gameService.drawCardForDealer(gameId, playerId);
            if (tmpMap == null) {
                return Response.status(Constants.CODE_NOT_MODIFIED).build();
            }
            dealersCardMap.put(tmpMap.keySet().iterator().next(), tmpMap.values().iterator().next());
            //Update game state to recount players score.
            gameService.updateGame(gameId);
        }



        String gameRoundResult = gameService.gameRoundResult(gameId, playerId, gameService.getRoundsBet(gameId, playerId), false);
        JSONObject returnObject = new JSONObject();
        returnObject.put("isRoundFinished", true);
        returnObject.put("gameRoundResult", gameRoundResult);
        returnObject.put("dealersDeckScore", gameService.dealersDeckScore(gameId));
        returnObject.put("drawnCard", helper.parseDrawnCardToJSON(dealersCardMap));
        returnObject.put("newBalance", gameService.getPlayersBalance(playerId));
        return Response.status(Constants.CODE_OK).entity(returnObject).build();
    }


    @POST
    @Path("/modifyBalance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyBalance(final String input) {
        JSONObject inputObject = helper.parse(input);

        Integer playerId = ((Long)inputObject.get("playerId")).intValue();
        Integer sum = ((Long)inputObject.get("sum")).intValue();

        Integer newBalance = gameService.addBalance(playerId, sum);

        if (newBalance == null) {
            return Response.status(Constants.CODE_NOT_MODIFIED).build();
        } else {
            JSONObject returnObject = new JSONObject();
            returnObject.put("newBalance", newBalance);
            return Response.status(Constants.CODE_CREATED).entity(returnObject).build();
        }
    }

    @GET
    @Path("/start")
    @Produces(MediaType.APPLICATION_JSON)
    public Response onGameStart(@QueryParam("playerId") final Long player) {
        Integer playerId = player.intValue();

        Integer gameId = gameService.onGameStart(playerId);

        if (gameId == null) {
            return Response.status(Constants.CODE_NOT_MODIFIED).build();
        } else {
            JSONObject returnObject = new JSONObject();
            returnObject.put("gameId", gameId);
            return Response.status(Constants.CODE_CREATED).entity(returnObject).build();
        }
    }
}
