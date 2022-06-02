package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public static Sections of(Station upStation, Station downStation, Distance distance) {
        List<Section> sections = new ArrayList<>();
        sections.add(new Section(null, upStation, null));
        sections.add(new Section(upStation, downStation, distance));
//        sections.add(new Section(downStation, null, null));
        return new Sections(sections);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void add(Section newSection) {
        Optional<Section> upMatchedSection = matchUpStation(newSection);
        if (upMatchedSection.isPresent()) {
            addSectionOfUpMatchedCase(newSection, upMatchedSection);
            return;
        }

        Optional<Section> downMatchedSection = matchDownStation(newSection);
        if (downMatchedSection.isPresent()) {
            addSectionOfDownMatchedCase(newSection, downMatchedSection);
            return;
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Station preStation = getFirstSection().getDownStation();

        stations.add(preStation);
        while (true) {
            Station finalPreStation = preStation;
            Optional<Station> nextStation = sections.stream()
                    .filter(section -> !Objects.isNull(section.getUpStation()))
                    .filter(section -> section.getUpStation().getId().equals(finalPreStation.getId()))
                    .map(Section::getDownStation)
                    .findFirst();

            if (nextStation.isPresent()) {
                stations.add(nextStation.get());
                preStation = nextStation.get();
            }

            if (!nextStation.isPresent()) {
                break;
            }
        }

        return stations;
    }


    private Section getFirstSection() {
        return sections.stream()
                .filter(section -> Objects.isNull(section.getUpStation()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Section getLastSection() {
        return sections.stream()
                .filter(section -> Objects.isNull(section.getDownStation()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Optional<Section> matchDownStation(Section newSection) {
        return sections.stream()
                .filter(section -> !Objects.isNull(section.getDownStation()))
                .filter(section -> section.getDownStation().getId().equals(newSection.getDownStation().getId()))
                .findFirst();
    }

    private void addSectionOfDownMatchedCase(Section newSection, Optional<Section> downMatchedSection) {
        Section oldSection = downMatchedSection.get();
        sections.add(
                new Section(
                        newSection.getUpStation(),
                        oldSection.getDownStation(),
                        oldSection.getDistance()
                )
        );
        oldSection.setDownStation(newSection.getUpStation());
        oldSection.setDistance(oldSection.getDistance().minus(newSection.getDistance()));
    }

    private Optional<Section> matchUpStation(Section newSection) {
        Optional<Section> upMatchedSection = sections.stream()
                .filter(section -> !Objects.isNull(section.getUpStation()))
                .filter(section -> section.getUpStation().getId().equals(newSection.getUpStation().getId()))
                .findFirst();
        return upMatchedSection;
    }

    private void addSectionOfUpMatchedCase(Section newSection, Optional<Section> upMatchedSection) {
        Section oldSection = upMatchedSection.get();
        sections.add(
                new Section(
                        newSection.getDownStation(),
                        oldSection.getDownStation(),
                        oldSection.getDistance().minus(newSection.getDistance())
                )
        );
        oldSection.setDownStation(newSection.getDownStation());
        oldSection.setDistance(newSection.getDistance());
    }
}
