package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import nextstep.subway.station.domain.Station;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    Station station;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Section prevSection;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Section nextSection;

    protected Section() {
    }

    public Section(Station station) {
        this.station = station;
    }

    public Section withNextSection(Section nextSection) {
        if (nextSection.prevSection == this) {
            return this;
        }

        this.nextSection = nextSection.withPrevSection(this); // TODO 양방향 관계에 대해 고민
        return this;
    }

    public Section withPrevSection(Section prevSection) {
        if (prevSection.nextSection == this) {
            return this;
        }

        this.prevSection = prevSection.withNextSection(this); // TODO 양방향 관계에 대해 고민
        return this;
    }
}
