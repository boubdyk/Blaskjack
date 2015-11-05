package bs.bj.dao;

import bs.bj.entity.EPlayingDeck;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by boubdyk on 01.11.2015.
 */

@Named
public class PlayingDeckDAO implements GenericDAO<EPlayingDeck, Integer>{

    @PersistenceContext(unitName = "entityManager")
    private EntityManager entityManager;

    public PlayingDeckDAO(){}

    @Override
    public Integer create(EPlayingDeck newInstance) {
        entityManager.persist(newInstance);
        return newInstance.getId();
    }

    @Override
    public EPlayingDeck read(Integer id) {
        return entityManager.find(EPlayingDeck.class, id);
    }

    @Override
    public EPlayingDeck update(EPlayingDeck transientObject) {
        entityManager.merge(transientObject);
        return transientObject;
    }

    @Override
    public boolean delete(Integer persistentObjectID) {
        entityManager.remove(read(persistentObjectID));
        return read(persistentObjectID) == null ? true : false;
    }

    /**
     * Used to get playing deck for current game round.
     * @param gameId game round identifier.
     * @return playing deck.
     */
    public List<EPlayingDeck> getDeck(Integer gameId) {
        String query = "SELECT d FROM EPlayingDeck d WHERE d.gameId=" + gameId;
        TypedQuery<EPlayingDeck> result = entityManager.createQuery(query, EPlayingDeck.class);
        return result.getResultList();
    }

    /**
     * Used to get card identifier from playing deck.
     *
     * @param gameID game round identifier.
     * @param suit cards suit.
     * @param face cards face.
     * @return card id.
     */
    public Integer getCardID(Integer gameID, String suit, String face) {
        String query = "SELECT d.id FROM EPlayingDeck d WHERE d.gameId=" +
                gameID + " AND d.cardSuit=" +
                "\'" + suit + "\'" + " AND d.cardFace=" +
                "\'" + face + "\'";
        TypedQuery<Integer> result = entityManager.createQuery(query, Integer.class);
        return result.getSingleResult();
    }
}
