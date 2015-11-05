package bs.bj.service;

import bs.bj.entity.EDealersDeck;
import bs.bj.entity.EPlayersDeck;
import bs.bj.deck.Card;
import bs.bj.deck.Face;
import bs.bj.deck.Suit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.inject.Named;
import java.util.Map;

/**
 * Created by boubdyk on 04.11.2015.
 */

@Named
public class Helper {

    public Helper() {}


    /**
     * @param input json file which need to parse
     * @return JSONObject
     */
    public JSONObject parse(final String input) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = (Object)parser.parse(input);
            return (JSONObject) obj;
        } catch (ParseException pe) {
            pe.printStackTrace();
            return null;
        }
    }


    //Uses while firs two cards dealing.
    public JSONArray parseCardsToJSON(Map<EPlayersDeck, EDealersDeck> map) {
        JSONObject jsonCardItem;
        Card card;
        JSONArray returnJsonArray = new JSONArray();
        for (Map.Entry<EPlayersDeck, EDealersDeck> entry: map.entrySet()) {
            jsonCardItem = new JSONObject();
            card = new Card(Suit.valueOf(entry.getKey().getCardSuit().toString()), Face.valueOf(entry.getKey().getCardFace().toString()));
            jsonCardItem.put("playersCard", card);
            card = new Card(Suit.valueOf(entry.getValue().getCardSuit().toString()), Face.valueOf(entry.getValue().getCardFace().toString()));
            jsonCardItem.put("dealersCard", card);
            returnJsonArray.add(jsonCardItem);
        }
        return returnJsonArray;
    }

    public JSONArray parseDrawnCardToJSON(EPlayersDeck ePlayersDeck) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cardSuit", ePlayersDeck.getCardSuit());
        jsonArray.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("cardFace", ePlayersDeck.getCardFace());
        jsonArray.add(jsonObject);
        return jsonArray;
    }

    public JSONArray parseDrawnCardToJSON(Map<EDealersDeck, Integer> dealersCardMap) {
        JSONArray returnArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for (EDealersDeck item: dealersCardMap.keySet()) {
            jsonObject.put("cardSuit", item.getCardSuit());
            jsonObject.put("cardFace", item.getCardFace());
            returnArray.add(jsonObject);
        }
        return returnArray;
    }



}
