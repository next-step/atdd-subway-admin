package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.sectionstations.domain.SectionStation;
import nextstep.subway.sectionstations.domain.SectionStations;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @Embedded
    SectionStations sectionStations = new SectionStations();

    private int distance;

    public Section() {
    }

    private Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.distance = distance;

        this.sectionStations.add(new SectionStation(this, upStation));
        this.sectionStations.add(new SectionStation(this, downStation));
    }

    public static Section getInstance(Line line, Station upStation, Station downStation, int distance) {
        Section section = new Section(line, upStation, downStation, distance);
        line.addSection(section);
        return section;
    }

    public Long getId() {
        return id;
    }

    public List<Station> stations() {
        return sectionStations.stations();
    }
}
