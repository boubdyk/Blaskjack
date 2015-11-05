package bs.bj.service;

import bs.bj.dao.PlayerDAO;
import bs.bj.entity.EPlayer;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by boubdyk on 31.10.2015.
 */

@Named
@Transactional
public class PlayerService {

    @Inject
    private PlayerDAO playerDAO;

    public PlayerService() {}

    //Get players balance.
    public Integer getBalance(Integer playerID) {
        if (playerID == null) return null;
        EPlayer ePlayer = playerDAO.read(playerID);
        return ePlayer == null ? null : playerDAO.read(playerID).getBalance();
    }
}
