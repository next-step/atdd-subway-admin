package nextstep.subway.section.domain;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public int size() {
        return this.sections.size();
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .sorted()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void addSection(Section newSection) {
        checkValidSection(newSection);
        for (Section section : sections) {
            // 이 메서드에서 section 과 otherSection 의 상행역, 하행역을 검사해서 section 의 상행, 하행역을 업데이트 하면 될거 같아요!
            section.addInnerSection(newSection);
        }
        sections.add(newSection);
    }

    public void addUpdatedSection(Section section) {
        List<Station> stations = getStations();
        updateSection(stations, section);
        this.sections.add(section);
    }

    private void updateSection(List<Station> stations, Section newSection) {
        if (stations.contains(newSection.getUpStation())) {
            updateUpStation(newSection);
            return;
        }

        if (stations.contains(newSection.getDownStation())) {
            updateDownStation(newSection);
        }
    }

    private void updateDownStation(Section section) {
        sections.stream()
                .filter(it -> it.isSameDownStation(section))
                .findFirst()
                .ifPresent(it -> it.updateDownStation(section));
    }

    private void updateUpStation(Section section) {
        sections.stream()
                .filter(it -> it.isSameUpStation(section))
                .findFirst()
                .ifPresent(it -> it.updateUpStation(section));
    }

    private Section findMatchedDownStationInNewSection(Section newSection) {
        return sections.stream()
                .filter(it -> it.getDownStation() == newSection.getDownStation())
                .findFirst()
                .orElse(null);
    }

    private Section findMatchedUpStationInNewSection(Section newSection) {
        return sections.stream()
                .filter(it -> it.getUpStation() == newSection.getUpStation())
                .findFirst()
                .orElse(null);
    }

    private void checkValidSection(Section section) {
        if (isExist(section)) {
            throw new InputDataErrorException(InputDataErrorCode.THE_SECTION_ALREADY_EXISTS);
        }

//        if (!registerBothStationInLine(section)) {
//            throw new InputDataErrorException(InputDataErrorCode.THEY_ARE_NOT_SEARCHED_STATIONS);
//        }
    }

    private boolean registerBothStationInLine(Section section) {
        List<Station> stationsInSection = section.getStations();

        return getStations().stream()
                .anyMatch(it -> stationsInSection.contains(it));
    }

    private boolean isExist(Section section) {
        return this.sections.stream()
                .anyMatch(it -> isSameSection(section, it));
    }

    private boolean isSameSection(Section section, Section it) {
        return it.equals(section);
    }

    public List<Section> getSectionInOrder() {
        // 출발지점 찾기

        return getSections();
    }



    public List<Section> getSections() {
        return sections;
    }
}
