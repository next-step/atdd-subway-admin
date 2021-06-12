package nextstep.subway.station.domain;

import java.util.Objects;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.exception.LineEndpointException;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @OneToOne(mappedBy = "downStation")
    private Section prevSection;

    @OneToOne(mappedBy = "upStation")
    private Section nextSection;

    public Station() {
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

    public boolean isFirst() {
        return Objects.isNull(prevSection);
    }

    public boolean isLast() {
        return Objects.isNull(nextSection);
    }

    public Station prevStation() {
        if (Objects.isNull(prevSection)) {
            throw new LineEndpointException("이 역은 출발역입니다.");
        }

        return prevSection.getUpStation();
    }

    public Station nextStation() {
        if (Objects.isNull(nextSection)) {
            throw new LineEndpointException("이 역은 종착역입니다.");
        }

        return nextSection.getDownStation();
    }

    public void setPrevSection(Section prevSection) {
        this.prevSection = prevSection;
    }

    public void setNextSection(Section nextSection) {
        this.nextSection = nextSection;
    }
}
