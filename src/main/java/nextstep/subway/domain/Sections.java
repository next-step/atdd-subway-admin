package nextstep.subway.domain;

import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.InvalidParameterException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Sections {

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "line",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public void infix(Section infixSection) {
        checkContainsAllSectionStation(infixSection);
        Section needChangedStation = getNeedChangedStationSection(infixSection);
        needChangedStation.changed(infixSection);
        add(infixSection);
    }

    public void delete(Station station) {
        if (isHaveOnlyOneSection()) {
            throw new InvalidParameterException("단일 구간인 노선은 역을 제거할 수 없습니다.");
        }
        List<Section> sectionsContainStation = getSectionsContainStation(station);
        deleteStation(sectionsContainStation, station);
    }

    private boolean isHaveOnlyOneSection() {
        if (sections.size() < 2) {
            return true;
        }
        return false;
    }

    private void deleteStation(List<Section> sectionsContainStation, Station station) {
        if (sectionsContainStation.size() == 0) {
            throw new InvalidParameterException("호선에 존재하지 않는 역은 제거할 수 없습니다.");
        }

        if (sectionsContainStation.size() == 2) {
            sections.add(createDeleteSection(sectionsContainStation, station));
            sections.remove(sectionsContainStation.get(1));
        }
        sections.remove(sectionsContainStation.get(0));
    }

    private Section createDeleteSection(List<Section> sectionsContainStation, Station station) {
        return sectionsContainStation.get(0).delete(sectionsContainStation.get(1), station);
    }

    private List<Section> getSectionsContainStation(Station station) {
        List<Section> sectionsContainStation = new ArrayList<>();
        for (Section section : sections) {
            sectionsContainStation.add(getSectionContainStation(section, station));
            sectionsContainStation.remove(null);
        }
        return sectionsContainStation;
    }

    private Section getSectionContainStation(Section section, Station station) {
        if (section.getUpStation().equalsById(station)) {
            return section;
        }
        if (section.getDownStation().equalsById(station)) {
            return section;
        }
        return null;
    }

    private void checkContainsAllSectionStation(Section infixSection) {
        boolean containPreStation = false;
        boolean containPostStation = false;

        for (Section section : sections) {
            containPreStation = section.isContainsPreStation(containPreStation, infixSection);
            containPostStation = section.isContainsPostStation(containPostStation, infixSection);
        }

        if (containPreStation && containPostStation) {
            throw new InvalidParameterException("상행역과 하행역이 모두 노선에 등록되어 있는 경우 새롭게 등록할 수 없습니다.");
        }
    }

    public List<Section> getList() {
        return sections;
    }

    private Section getNeedChangedStationSection(Section infixSection) {
        Section changedStationSection = getInnerStationSection(infixSection);

        if (changedStationSection == null) {
            return getOuterStationSection(infixSection);
        }

        return changedStationSection;
    }

    private Section getInnerStationSection(Section infixSection) {
        Section changedStationSection = sections
                .stream()
                .filter(section -> section.isUpStationEqualsId(infixSection.getUpStation()))
                .findAny()
                .orElse(null);

        if (isNothingToChange(changedStationSection)) {
            return sections
                    .stream()
                    .filter(section -> section.isDownStationEqualsId(infixSection.getDownStation()))
                    .findAny()
                    .orElse(null);
        }
        return changedStationSection;
    }

    private Section getOuterStationSection(Section infixSection) {
        Section changedStationSection = sections
                .stream()
                .filter(section -> section.isUpStationEqualsId(infixSection.getDownStation()))
                .findAny()
                .orElse(null);

        if (isNothingToChange(changedStationSection)) {
            changedStationSection = sections
                    .stream()
                    .filter(section -> section.isDownStationEqualsId(infixSection.getUpStation()))
                    .findAny()
                    .orElse(null);
        }

        if (isNothingToChange(changedStationSection)) {
            throw new InvalidParameterException("상행역과 하행역 모두 노선에 등록되어있지 않습니다.");
        }

        return changedStationSection;
    }

    private boolean isNothingToChange(Section changedStationSection) {
        return changedStationSection == null;
    }

    public Set<StationResponse> sortSections() {
        Set<StationResponse> stationResponses = new LinkedHashSet<>();

        Section firstSection = findFirstSection();
        stationResponses.add(StationResponse.of(firstSection.getUpStation()));
        for (int i = 0; i < sections.size(); i++) {
            firstSection = addedNextSectionStations(firstSection, stationResponses);
        }

        return stationResponses;
    }

    private Section addedNextSectionStations(Section firstSection,
                                          Set<StationResponse> stationResponses) {
        for (Section section : sections) {
            firstSection = resetNextSection(section, firstSection);
            stationResponses.add(StationResponse.of(firstSection.getUpStation()));
            stationResponses.add(StationResponse.of(firstSection.getDownStation()));
        }
        return firstSection;
    }

    private Section resetNextSection(Section section, Section firstSection) {
        if (section.isPostStation(firstSection)) {
            return section;
        }
        return firstSection;
    }

    private Section findFirstSection() {
        Section firstSection = sections.get(0);
        for (int i = 0; i < sections.size(); i++) {
            firstSection = findPreSection(firstSection);
        }
        return firstSection;
    }

    private Section findPreSection(Section firstSection) {
        for (Section section : sections) {
            firstSection = resetFirstSection(section, firstSection);
        }
        return firstSection;
    }

    private Section resetFirstSection(Section section, Section firstSection) {
        if (section.isPreStation(firstSection)) {
            return section;
        }
        return firstSection;
    }

}
