package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lineId")
    private Line line;

    protected Station() {}

    public Station(String name) {
        this.name = name;
    }

    public void toLine(Line line) {
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
