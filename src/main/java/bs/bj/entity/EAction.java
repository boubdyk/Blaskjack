package bs.bj.entity;

import javax.persistence.*;

/**
 * Created by boubdyk on 30.10.2015.
 */

@Entity
@Table(name = "ACTION")
public class EAction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Column(name = "description")
    private String description;

    public EAction(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
