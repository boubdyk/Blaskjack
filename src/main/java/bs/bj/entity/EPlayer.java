package bs.bj.entity;


import javax.persistence.*;
import java.util.List;

/**
 * Created by boubdyk on 30.10.2015.
 */

@Entity
@Table(name = "PLAYER")
public class EPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Column(name = "balance")
    private Integer balance;

    public EPlayer(){}

    public EPlayer(Integer balance) {
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

}
