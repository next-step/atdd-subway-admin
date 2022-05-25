package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @OneToOne
    @JoinColumn(name = "id")
    private Line upLine;

    @OneToOne
    @JoinColumn(name = "id")
    private Line downLine;

    private Integer distance;

    protected Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setUpLine(Line upLine) {
        this.upLine = upLine;
    }

    public void setDownLine(Line downLine) {
        this.downLine = downLine;
    }
}
