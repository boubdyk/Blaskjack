package bs.bj.dao;

import bs.bj.entity.EGame;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by boubdyk on 30.10.2015.
 */

@Named
public class GameDAO implements GenericDAO<EGame, Integer> {

    @PersistenceContext(unitName = "entityManager")
    private EntityManager entityManager;

    public GameDAO(){}

    @Override
    public Integer create(EGame newInstance) {
        entityManager.persist(newInstance);
        return newInstance.getId();
    }

    @Override
    public EGame read(Integer id) {
        return entityManager.find(EGame.class, id);
    }

    @Override
    public EGame update(EGame transientObject) {
        entityManager.merge(transientObject);
        return transientObject;
    }

    @Override
    public boolean delete(Integer persistentObjectID) {
        entityManager.remove(persistentObjectID);
        return read(persistentObjectID) == null ? true : false;
    }

    /**
     * This method is used to get winner column from Game table for current game.
     *
     * @param gameId unique game identifier.
     * @return winner column.
     */
    public String getWinner(Integer gameId) {
        String query = "SELECT g.winner FROM EGame g WHERE g.id=" + gameId;
        TypedQuery<String> result = entityManager.createQuery(query, String.class);
        return result.getSingleResult();
    }
}
