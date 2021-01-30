package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
        CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public Sections(Line line, Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(line, upStation, downStation, distance));
    }


    public List<Section> getStations() {
        return sections;
    }

    public void add(Section newSection) {
        validateAlreadyUsedSection(newSection);
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        if (this.anyMatch(upStation)) {
            this.updateDownStation(newSection);
            this.sections.add(newSection);
            return;
        }
        if (this.anyMatch(downStation)) {
            this.updateUpStation(newSection);
            this.sections.add(newSection);
            return;
        }

        this.sections.add(newSection);
    }

    private void updateDownStation(Section newSection) {
        getSectionByUpStation(newSection.getUpStation())
            .ifPresent(it -> it.updateUpStation(newSection.getDownStation(), newSection.getDistance()));
    }

    private void updateUpStation(Section newSection) {
        getSectionByDownStation(newSection.getDownStation())
            .ifPresent(it -> it.updateDownStation(newSection.getUpStation(), newSection.getDistance()));
    }

    private void validateAlreadyUsedSection(Section section) {
        if (this.anyMatch(section.getUpStation()) && this.anyMatch(section.getDownStation())) {
            throw new IllegalArgumentException("이미 등록된 구간 입니다.");
        }
    }

    private Optional<Section> getSectionByUpStation(Station station) {
        return this.sections.stream()
            .filter(it -> it.getUpStation() == station)
            .findFirst();
    }

    public Optional<Section> getSectionByDownStation(Station station) {
        return this.sections.stream()
            .filter(it -> it.getDownStation() == station)
            .findFirst();
    }

    public boolean anyMatch(Station station) {
        return Stream
            .concat(sections.stream().map(Section::getUpStation), sections.stream().map(Section::getDownStation))
            .anyMatch(it -> it == station);
    }
}
