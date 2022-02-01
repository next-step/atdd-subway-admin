package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.Embeddable;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSections(Line line, Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(line, upStation, downStation, distance));
    }

    public List<Section> getSections() {

        return Collections.unmodifiableList(sections);
    }


}
