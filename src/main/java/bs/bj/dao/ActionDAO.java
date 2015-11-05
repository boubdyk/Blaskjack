package bs.bj.dao;

import bs.bj.entity.EAction;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * Created by boubdyk on 30.10.2015.
 */

@Named
public class ActionDAO implements GenericDAO<EAction, Integer> {

    @PersistenceContext(unitName = "entityManager")
    private EntityManager entityManager;

    public ActionDAO() {}

    @Override
    public Integer create(EAction newInstance) {
        entityManager.persist(newInstance);
        return newInstance.getId();
    }

    @Override
    public EAction read(Integer id) {
        return entityManager.find(EAction.class, id);
    }

    @Override
    public EAction update(EAction transientObject) {
        return null;
    }

    @Override
    public boolean delete(Integer persistentObjectID) {
        return false;
    }



    /**
     * Returns action id by action name.
     *
     * @param actionName the name of action.
     * @return action id.
     */
    public Integer getID(String actionName) {
        String query = "SELECT a.id FROM EAction a WHERE a.description=\'" + actionName + "\'";
        TypedQuery<Integer> result = entityManager.createQuery(query, Integer.class);
        return result.getSingleResult();
    }
}
