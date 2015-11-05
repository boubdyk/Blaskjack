package bs.bj.dao;

import java.io.Serializable;

/**
 * Created by boubdyk on 30.10.2015.
 */
public interface GenericDAO <T, PK extends Serializable>{

    //Save newInstance object to DataBase
    PK create(T newInstance);

    //Get object from DataBase using id like PK
    T read(PK id);

    //Save changes to DataBase that was made in object
    T update(T transientObject);

    //Delete object from DataBase
    boolean delete(PK persistentObjectID);
}
