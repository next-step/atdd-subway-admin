package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            this.sections.add(section);
            return;
        }

        addNewSectionToOldSection(section);
    }

    public void addNewSectionToOldSection(Section newSection) {
        Station newUpStation = newSection.getUpStation();
        Station newDownStation = newSection.getDownStation();
        SectionDistance newDistance = newSection.getDistance();

        for (Section oldSection : sections) {
            Station oldDownStation = oldSection.getDownStation();
            SectionDistance oldDistance = oldSection.getDistance();

            if (newSection.isBetweenAndUpStationSameWith(oldSection)) {
                oldDistance.checkDistanceWith(newDistance);
                oldSection.updateBy(newDownStation, newDistance);
                this.sections.add(new Section(newDownStation, oldDownStation, oldDistance.minus(newDistance)));
                return;
            }

            if (newSection.isBetweenAndDownStationSameWith(oldSection)) {
                oldDistance.checkDistanceWith(newDistance);
                oldSection.updateBy(newUpStation, oldDistance.minus(newDistance));
                this.sections.add(new Section(newUpStation, newDownStation, newDistance));
                return;
            }

            if (newSection.isAtTheEndWith(oldSection)) {
                this.sections.add(new Section(newUpStation, newDownStation, newDistance));
                return;
            }
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    public Stations getAllStations() {
        return new Stations(this);
    }
}
