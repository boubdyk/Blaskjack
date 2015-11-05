package bs.bj.service;

import bs.bj.dao.HistoryDAO;
import bs.bj.dao.PlayerDAO;
import bs.bj.entity.EHistory;
import bs.bj.entity.EPlayer;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by boubdyk on 31.10.2015.
 */

@Named
@Transactional
public class HistoryService {

    @Inject
    private HistoryDAO historyDAO;

    public HistoryService() {}

    /**
     * Used to add row to table History.
     * @param playerId players identifier.
     * @param gameId game round identifier.
     * @param actionId action identifier.
     * @return id of new row in table History.
     */
    public Integer addHistory(Integer playerId, Integer gameId, Integer actionId) {
        EHistory eHistory = new EHistory(actionId, playerId, gameId);
        return historyDAO.create(eHistory);
    }
}
