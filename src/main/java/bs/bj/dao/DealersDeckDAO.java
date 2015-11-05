package bs.bj.dao;

import bs.bj.entity.EDealersDeck;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by boubdyk on 01.11.2015.
 */

@Named
public class DealersDeckDAO implements GenericDAO<EDealersDeck, Integer> {

    @PersistenceContext(unitName = "entityManager")
    private EntityManager entityManager;

    public DealersDeckDAO() {}

    @Override
    public Integer create(EDealersDeck newInstance) {
        entityManager.persist(newInstance);
        return newInstance.getId();
    }

    @Override
    public EDealersDeck read(Integer id) {
        return entityManager.find(EDealersDeck.class, id);
    }

    @Override
    public EDealersDeck update(EDealersDeck transientObject) {
        entityManager.merge(transientObject);
        return transientObject;
    }

    @Override
    public boolean delete(Integer persistentObjectID) {
        entityManager.remove(read(persistentObjectID));
        return read(persistentObjectID) == null ? true : false;
    }


    /**
     * This method is used to get list of all cards of dealer for current game.
     *
     * @param gameId unique game identifier.
     * @return list of dealers cards for current game round.
     */
    public List<EDealersDeck> getCards(Integer gameId) {
        String query = "SELECT d FROM EDealersDeck d WHERE d.gameId=" + gameId;
        TypedQuery<EDealersDeck> result = entityManager.createQuery(query, EDealersDeck.class);
        return result.getResultList();
    }
}
