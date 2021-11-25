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

    public void addSection(Section newSection) {
        Section upStation = sections.stream()
                .filter(it -> it.getUpStation() == newSection.getUpStation())
                .findFirst()
                .orElse(null);

        Section downStation = sections.stream()
                .filter(it -> it.getDownStation() == newSection.getDownStation())
                .findFirst()
                .orElse(null);
        createSection(newSection, upStation, downStation);
    }

    private void createSection(Section newSection, Section upStation, Section downStation) {

        if (upStation == null && downStation == null) {
            //하행종점 A-B-C-NEW
            this.sections.add(newSection);
        }

        //상행 종점 NEW-A-B-C
        if (isAddFirstSection(upStation, downStation)) {
            addUpFirstSection(newSection, downStation);
        }

        if (isMiddleLeftSection(upStation, downStation)) {
            //중간 A-NEW-B-C
            if (newSection.getDistance() >= downStation.getDistance()) {
                throw new InputDataErrorException(InputDataErrorCode.DISTANCE_OF_THE_OLD_SECTION_IS_LESS_THAN_NEW_SECTION_DISTANCE);
            }
            addMiddleLeftSection(newSection, downStation);
        }

        if (isMiddleRightSection(upStation, downStation)) {
            //중간 A-B-NEW-C
            if (newSection.getDistance() >= upStation.getDistance()) {
                throw new InputDataErrorException(InputDataErrorCode.DISTANCE_OF_THE_OLD_SECTION_IS_LESS_THAN_NEW_SECTION_DISTANCE);
            }
            addMiddleRightSection(newSection, upStation);
        }


    }

    private boolean isMiddleRightSection(Section upStation, Section downStation) {
        return upStation != null && upStation.getDownStation() != null && downStation == null;
    }

    private boolean isMiddleLeftSection(Section upStation, Section downStation) {
        return upStation == null && downStation != null && downStation.getUpStation() != null;
    }

    private boolean isAddFirstSection(Section upStation, Section downStation) {
        return upStation == null && downStation != null && downStation.getUpStation() == null;
    }

    private void addMiddleRightSection(Section newSection, Section upStation) {
        Section upSection = new Section(upStation.getLine(), upStation.getUpStation(), newSection.getDownStation(), newSection.getDistance());
        Section downSection = new Section(upStation.getLine(), newSection.getDownStation(), upStation.getDownStation(), upStation.getDistance() - newSection.getDistance());
        int insertIndex = upStation.getId().intValue() - 1;

        sections.remove(insertIndex);
        sections.add(insertIndex, upSection);
        sections.add(insertIndex + 1, downSection);
    }

    private void addMiddleLeftSection(Section newSection, Section downStation) {
        Section upSection = new Section(downStation.getLine(), downStation.getUpStation(), newSection.getUpStation(), downStation.getDistance() - newSection.getDistance());
        Section downSection = new Section(downStation.getLine(), newSection.getUpStation(), downStation.getDownStation(), newSection.getDistance());
        int insertIndex = downStation.getId().intValue() - 1;

        sections.remove(insertIndex);
        sections.add(insertIndex, upSection);
        sections.add(insertIndex + 1, downSection);
    }

    private void addUpFirstSection(Section newSection, Section downStation) {
        downStation.updateUpStationTo(newSection.getUpStation());
        downStation.updateDistance(newSection.getDistance());

        Section emptySection = new Section(newSection.getLine(), null, downStation.getUpStation(), 0);
        addFirstSection(emptySection);
    }

    private void addFirstSection(Section section) {
        this.sections.add(0, section);
    }


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
