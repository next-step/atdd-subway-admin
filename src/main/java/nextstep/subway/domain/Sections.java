package nextstep.subway.domain;

import com.google.common.collect.Lists;
import nextstep.subway.exception.CannotAddSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static nextstep.subway.exception.CannotAddSectionException.NO_MATCHED_STATION;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sectionList = new LinkedList<>();

    protected Sections() {
    }

    public Sections(Section... sections) {
        sectionList.addAll(Lists.newArrayList(sections));
    }

    public void addSection(Line line, Station upStation, Station downStation, Distance distance) {
        if (sectionList.isEmpty()) {
            sectionList.add(new Section(line, upStation, downStation, distance));
            return;
        }
        Section previousSection = getPreviousSection(upStation, downStation);
        List<Section> appendedSections = previousSection.addSection(upStation, downStation, distance);

        replaceSection(previousSection, appendedSections);
    }

    private Section getPreviousSection(Station upStation, Station downStation) {
        return sectionList.stream()
                .filter(section -> section.canAddSection(upStation, downStation))
                .findFirst()
                .orElseThrow(() -> new CannotAddSectionException(NO_MATCHED_STATION));
    }

    private void replaceSection(Section previousSection, List<Section> appendedSections) {
        int previousLineStationIndex = sectionList.indexOf(previousSection);
        sectionList.remove(previousLineStationIndex);
        sectionList.addAll(previousLineStationIndex, appendedSections);
    }

    public Stream<Section> stream() {
        return sectionList.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections that = (Sections) o;
        return sectionList.equals(that.sectionList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionList);
    }

    @Override
    public String toString() {
        return "LineStations{" +
                "lineStationList=" + sectionList +
                '}';
    }
}
