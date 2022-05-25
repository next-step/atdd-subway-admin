package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToOne(mappedBy = "downStation")
    private Line upLine;

    @OneToOne(mappedBy = "upStation")
    private Line downLine;

    private Integer distance;

    protected Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Station(String name, Line upLine, Line downLine) {
        this.name = name;
        this.upLine = upLine;
        this.downLine = downLine;
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

    public void clearDownLine() {
        this.downLine = null;
    }

    public void clearUpLine() {
        this.upLine = null;
    }
}
