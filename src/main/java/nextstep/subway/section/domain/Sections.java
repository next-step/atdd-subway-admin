package nextstep.subway.section.domain;

import nextstep.subway.section.exception.InvalidAddSectionException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private final List<Section> sections = new LinkedList<>();

    public void addInitSection(Section section) {
        sections.add(section);
    }

    public void addSection(Section section) {
        validate(section);
        compareStations(section);
    }

    public List<StationResponse> convertToStationResponses() {
        Set<StationResponse> stations = new LinkedHashSet<>();
        sections.forEach(section -> {
            stations.add(StationResponse.of(section.getUpStation()));
            stations.add(StationResponse.of(section.getDownStation()));
        });
        return new ArrayList<>(stations);
    }

    public List<Station> convertToStations() {
        Set<Station> stations = new LinkedHashSet<>();
        sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });
        return new ArrayList<>(stations);
    }

    private void validate(Section section) {
        checkEqualSection(section);
        checkContainStations(section);
    }

    private void compareStations(Section section) {
        if (isAddSectionCompareUpUp(section)) {
            return;
        }
        if (isAddSectionCompareDownDown(section)) {
            return;
        }
        if (isAddSectionCompareUpDown(section)) {
            return;
        }
        isAddSectionCompareDownUp(section);
    }

    private boolean isAddSectionCompareUpUp(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.equalUpUpStation(section))
                .findFirst()
                .ifPresent(oldSection -> {
                    checkDistance(oldSection, section);

                    int index = sections.indexOf(oldSection);
                    sections.add(index, section);
                    oldSection.updateUpStation(section.getDownStation(), oldSection.minusDistance(section));
                });
        return sections.indexOf(section) != -1;
    }

    private boolean isAddSectionCompareDownDown(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.equalDownDownStation(section))
                .findFirst()
                .ifPresent(oldSection -> {
                    checkDistance(oldSection, section);

                    int index = sections.indexOf(oldSection);
                    oldSection.updateDownStation(section.getUpStation(), oldSection.minusDistance(section));
                    sections.add(index + 1, section);
                });
        return sections.indexOf(section) != -1;
    }

    private boolean isAddSectionCompareUpDown(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.equalUpDownStation(section))
                .findFirst()
                .ifPresent(oldSection -> {
                    int index = sections.indexOf(oldSection);
                    sections.add(index, section);
                });
        return sections.indexOf(section) != -1;
    }

    private boolean isAddSectionCompareDownUp(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.equalDownUpStation(section))
                .findFirst()
                .ifPresent(oldSection -> {
                    int index = sections.indexOf(oldSection);
                    sections.add(index + 1, section);
                });
        return sections.indexOf(section) != -1;
    }

    private void checkEqualSection(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.equalStations(section))
                .findFirst()
                .ifPresent(oldSection -> {
                    throw new InvalidAddSectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
                });
    }

    private void checkContainStations(Section section) {
        List<Station> stations = convertToStations();
        if(!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new InvalidAddSectionException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
    }

    private void checkDistance(Section oldSection, Section newSection) {
        if (newSection.isEqualOrMoreDistance(oldSection)) {
            throw new InvalidAddSectionException("기존 역 사이 길이보다 크거나 같으면 등록을 할 수가 없습니다.");
        }
    }
}
