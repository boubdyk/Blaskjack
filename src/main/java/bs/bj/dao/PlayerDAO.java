package bs.bj.dao;

import bs.bj.entity.EPlayer;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by boubdyk on 30.10.2015.
 */

@Named
public class PlayerDAO implements GenericDAO<EPlayer, Integer> {

    @PersistenceContext(unitName = "entityManager")
    private EntityManager entityManager;

    public PlayerDAO() {}

    @Override
    public Integer create(EPlayer newInstance) {
        entityManager.persist(newInstance);
        return newInstance.getId();
    }

    @Override
    public EPlayer read(Integer id) {
        return entityManager.find(EPlayer.class, id);
    }

    @Override
    public EPlayer update(EPlayer transientObject) {
        entityManager.merge(transientObject);
        return transientObject;
    }

    @Override
    public boolean delete(Integer persistentObjectID) {
        entityManager.remove(persistentObjectID);
        return read(persistentObjectID) == null ? true : false;
    }
}
