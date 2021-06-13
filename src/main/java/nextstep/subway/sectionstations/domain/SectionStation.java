package nextstep.subway.sectionstations.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class SectionStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    public SectionStation() {
    }

    public SectionStation(Section section, Station station) {
        this.section = section;
        this.station = station;
    }

    public Station getStation() {
        return station;
    }
}
