package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(Section section) {
        sections.add(section);
    }

    public List<Station> stations() {
        return sections.stream()
            .flatMap(section -> section.stations().stream())
            .distinct()
            .collect(Collectors.toList());
    }

    public void add(Section section) {
        validStations(section);
        validDistance(section);
        splitSectionBy(section);
        sections.add(insertIndex(section), section);
    }

    private void validStations(Section section) {
        boolean isUpStationExist = contains(section.getUpStation());
        boolean isDownStationExist = contains(section.getDownStation());

        if (isUpStationExist && isDownStationExist) {
            throw new IllegalArgumentException("이미 추가한 구간입니다.");
        }

        if (!isUpStationExist && !isDownStationExist) {
            throw new IllegalArgumentException("구간을 추가할 수 없습니다.");
        }
    }

    private void validDistance(Section section) {
        if (section.equalDownStation(headStation()) || section.equalUpStation(tailStation())) {
            return;
        }

        int distance = section.getDistance();
        int limitDistance = sections.get(sectionIndex(section)).getDistance();

        if (limitDistance <= distance) {
            throw new IllegalArgumentException("구간 거리가 너무 깁니다.");
        }
    }

    private void splitSectionBy(Section section) {
        if (section.equalDownStation(headStation()) || section.equalUpStation(tailStation())) {
            return;
        }

        Section oldSection = sections.get(sectionIndex(section));

        if (oldSection.equalUpStation(section.getUpStation())) {
            oldSection.updateUpStation(section.getDownStation());
        }

        if (oldSection.equalDownStation(section.getDownStation())) {
            oldSection.updateDownStation(section.getUpStation());
        }
    }

    private boolean contains(Station station) {
        return stations().contains(station);
    }

    private int sectionIndex(Section section) {
        int index = IntStream.range(0, stations().size())
            .filter(i -> section.contains(stations().get(i)))
            .findFirst()
            .orElseThrow(EntityNotFoundException::new);

        if (contains(section.getDownStation())) {
            index -= 1;
        }

        return index;
    }

    private int insertIndex(Section section) {
        if (section.equalDownStation(headStation())) {
            return 0;
        }
        if (section.equalUpStation(tailStation())) {
            return sections.size();
        }

        int index = sectionIndex(section);
        if (sections.get(index).equalDownStation(section.getDownStation())) {
            return index + 1;
        }
        return index;
    }

    private Station headStation() {
        return stations().get(0);
    }

    private Station tailStation() {
        return stations().get(stations().size() - 1);
    }

}
