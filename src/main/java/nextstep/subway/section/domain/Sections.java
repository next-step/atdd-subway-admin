package nextstep.subway.section.domain;

import nextstep.subway.common.exception.MyException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int MINIMUM_NUMBER = 2;
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Section section) {
        sections.add(section);
    }

    public void add(Section section) {
        validateSection(section);

        findUpStation(section.getUpStation())
                .ifPresent(it -> it.updateUpStation(section));
        findDownStation(section.getDownStation())
                .ifPresent(it -> it.updateDownStation(section));
        sections.add(section);
    }

    public Optional<Section> findDownStation(Station downStation) {
        return sections.stream()
                .filter(it -> it.equalDownStation(downStation))
                .findFirst();
    }

    public Optional<Section> findUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.equalUpStation(upStation))
                .findFirst();
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(Section::getStations)
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateSection(Section section) {
        List<Station> stations = getStations();
        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
            throw new MyException("이미 등록된 구간 입니다.");
        }

        if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new MyException("등록할 수 없는 구간입니다.");
        }
    }

    public void removeSection(Long stationId) {
        List<Section> sectionList = findSectionByStationId(stationId);
        minimumNumberSectionsException(sectionList.size());

        Section upSection = sectionList.get(0);
        Section downSection = sectionList.get(1);
        sections.add(upSection.merge(downSection));
        sectionList.forEach(sections::remove);
    }

    private List<Section> findSectionByStationId(Long stationId) {
        return this.sections.stream()
                .filter(section -> section.isEqualsUpStation(stationId) || section.isEqualsDownStation(stationId))
                .collect(Collectors.toList());
    }

    private void minimumNumberSectionsException(int sectionSize) {
        if(sectionSize < MINIMUM_NUMBER) {
            throw new MyException("1개의 구간만 등록되어 있으므로 삭제가 불가능합니다.");
        }
    }
}