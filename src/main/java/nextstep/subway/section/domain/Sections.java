package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {

        boolean isUpStationExisted = isUpStationExisted(section);
        boolean isDownStationExisted = isDownStationExisted(section);

        checkStations(section, isUpStationExisted, isDownStationExisted);

        if (isUpStationExisted) {
            addSectionBasedOnUpStation(section);
        }

        if (isDownStationExisted) {
            addSectionBasedOnDownStation(section);
        }

        this.sections.add(section);
    }


    private void checkStations(Section section, boolean isUpStationExisted, boolean isDownStationExisted) {
        if (isUpStationExisted && isDownStationExisted) {
            throw new RuntimeException("상행역과 하행역 모두 이미 등록된 구간 입니다.");
        }

        if (!sections.isEmpty() && isUpStationNotExisted(section) && isDownStationNotExisted(section)) {
            throw new RuntimeException("등록할 수 없는 구간 입니다.");
        }
    }

    private boolean isDownStationNotExisted(Section section) {
        return sections.stream().noneMatch(it -> it.getDownStation() == section.getDownStation() ||
                it.getUpStation() == section.getDownStation());
    }

    private boolean isUpStationNotExisted(Section section) {
        return sections.stream().noneMatch(it -> it.getUpStation() == section.getUpStation() ||
                it.getDownStation() == section.getUpStation());
    }

    private boolean isDownStationExisted(Section section) {
        return sections.stream().anyMatch(it -> it.getDownStation() == section.getDownStation());
    }

    private boolean isUpStationExisted(Section section) {
        return sections.stream().anyMatch(it -> it.getUpStation() == section.getUpStation());
    }

    private void addSectionBasedOnDownStation(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getDownStation() == oldSection.getDownStation())
                .findFirst()
                .ifPresent(oldSection -> updateDownSections(section, oldSection));
    }

    private void addSectionBasedOnUpStation(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getUpStation())
                .findFirst()
                .ifPresent(oldSection -> updateSections(section, oldSection));
    }

    private void updateDownSections(Section section, Section oldSection) {
        if (oldSection.getUpStation() == null) {
            oldSection.changeDownStation(section.getUpStation());
            return;
        }

        int newDistance = oldSection.getDistanceIfInRange(section);
        sections.add(new Section(section.getLine(), section.getDownStation(), oldSection.getDownStation(), newDistance));
        sections.remove(oldSection);
    }

    private void updateSections(Section section, Section oldSection) {
        int newDistance = oldSection.getDistanceIfInRange(section);
        sections.add(new Section(section.getLine(), section.getDownStation(), oldSection.getDownStation(), newDistance));
        sections.remove(oldSection);
    }

    public List<Section> getSectionsInOrder() {
        List<Section> stations = new ArrayList<>();
        Optional<Section> upEndSection = findUpEndSection();

        while (upEndSection.isPresent()) {
            Section nextSection = upEndSection.get();

            stations.add(nextSection);
            upEndSection = findNextSection(nextSection.getDownStation());
        }
        return stations;

    }

    public List<Station> getStationsInOrder() {
        List<Station> stations = new ArrayList<>();
        Optional<Section> upEndSection = findUpEndSection();

        while (upEndSection.isPresent()) {
            Section nextSection = upEndSection.get();

            stations.add(nextSection.getDownStation());
            upEndSection = findNextSection(nextSection.getDownStation());
        }
        return stations;

    }

    private Optional<Section> findNextSection(Station downStation) {
        return sections.stream()
                .filter(it -> it.getUpStation() == downStation)
                .findFirst();
    }

    private Optional<Section> findUpEndSection() {
        return sections.stream()
                .filter(it -> it.getUpStation() == null)
                .findFirst();
    }

    public void addAll(Section upStationSection, Section downStationSection) {
        sections.addAll(Arrays.asList(upStationSection, downStationSection));
    }

    public List<Section> getSections() {
        return sections;
    }
}
