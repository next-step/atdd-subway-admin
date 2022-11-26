package nextstep.subway.domain;

import nextstep.subway.exception.InvalidParameterException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    private final static int MANY_SECTION_COUNT = 2;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "line",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {}

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

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
        Sections sectionsContainStation = new Sections(getSectionsContainStation(station));
        deleteStation(sectionsContainStation, station);
    }

    private boolean isHaveOnlyOneSection() {
        if (sections.size() < MANY_SECTION_COUNT) {
            return true;
        }
        return false;
    }

    private void deleteStation(Sections sectionsContainStation, Station station) {
        if (sectionsContainStation.isNotFoundStation()) {
            throw new InvalidParameterException("호선에 존재하지 않는 역은 제거할 수 없습니다.");
        }

        if (sectionsContainStation.isDeleteInnerStation()) {
            sections.add(createDeleteSection(sectionsContainStation, station));
            sections.remove(sectionsContainStation.sections.get(1));
        }
        sections.remove(sectionsContainStation.sections.get(0));
    }

    private boolean isDeleteInnerStation() {
        return sections.size() == 2;
    }

    private boolean isNotFoundStation() {
        return sections.isEmpty();
    }

    private Section createDeleteSection(Sections sectionsContainStation, Station station) {
        return sectionsContainStation.sections.get(0).delete(sectionsContainStation.sections.get(1), station);
    }

    private List<Section> getSectionsContainStation(Station station) {
        return sections.stream()
                .filter(section -> section.contains(station))
                .collect(Collectors.toList());
    }

    private void checkContainsAllSectionStation(Section infixSection) {
        boolean containPreStation = false;
        boolean containPostStation = false;

        for (Section section : sections) {
            boolean isContain = section.isContainsPreStation(infixSection);
            containPreStation = changeContainStationStatus(containPreStation, isContain);

            isContain = section.isContainsPostStation(infixSection);
            containPostStation = changeContainStationStatus(containPostStation, isContain);
        }

        if (containPreStation && containPostStation) {
            throw new InvalidParameterException("상행역과 하행역이 모두 노선에 등록되어 있는 경우 새롭게 등록할 수 없습니다.");
        }
    }

    private boolean changeContainStationStatus(boolean isAlreadyContain, boolean isContain) {
        if (isAlreadyContain) {
            return true;
        }
        return isContain;
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

    public Set<Station> sortStations() {
        Set<Station> stations = new LinkedHashSet<>();

        Section firstSection = findFirstSection();
        stations.add(firstSection.getUpStation());
        for (int i = 0; i < sections.size(); i++) {
            firstSection = addedNextSectionStations(firstSection, stations);
        }

        return stations;
    }

    private Section addedNextSectionStations(Section firstSection,
                                          Set<Station> stations) {
        for (Section section : sections) {
            firstSection = resetNextSection(section, firstSection);
            stations.add(firstSection.getUpStation());
            stations.add(firstSection.getDownStation());
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
