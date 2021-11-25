package nextstep.subway.section.domain;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public int size() {
        return this.sections.size();
    }

    public List<Station> getStations() {
        return this.sections.stream().
                map(Section::getDownStation).collect(Collectors.toList());
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

//    private boolean isAddUpLine(Section section) {
//        return sections.stream()
//                .filter(it -> it.getDownStation().equals(section.getUpStation()))
//                .findFirst()
//                .ifPresent(it-> it.add);
//    }

    private void checkValidSection(Section section) {
        if (isExist(section)) {
            throw new InputDataErrorException(InputDataErrorCode.THE_SECTION_ALREADY_EXISTS);
        }

        if (!enrollBothStationInLine(section)) {
            throw new InputDataErrorException(InputDataErrorCode.ONE_OF_THE_TWO_STATIONS_IS_NOT_REGISTERD_ON_LINE);
        }
    }

    private boolean enrollBothStationInLine(Section section) {
        return getStations().stream()
                .filter(it -> it.getId() == section.getUpStation().getId() || it.getId() == section.getDownStation().getId())
                .collect(Collectors.toList()).size() == 2;
    }

    private boolean isExist(Section section) {
        return this.sections.stream()
                .filter(it -> isSameSection(section, it))
                .findFirst().isPresent();
    }

    private boolean isSameSection(Section section, Section it) {
        return it.getUpStation().getId() == section.getUpStation().getId() && it.getDownStation().getId() == section.getDownStation().getId();
    }

    public List<Section> getSectionInOrder() {
        // 출발지점 찾기
        Optional<Section> upSection = sections.stream()
                .filter(it -> it.getDistance() == 0).findFirst();

        List<Section> result = new ArrayList<>();
        while (upSection.isPresent()) {
            Section foundSection = upSection.get();
            result.add(foundSection);
            upSection = sections.stream()
                    .filter(it -> it.getUpStation() != null && it.getUpStation().getId() == foundSection.getDownStation().getId())
                    .findFirst();
        }
        return result;
    }

    public List<Section> getSections() {
        return sections;
    }
}
