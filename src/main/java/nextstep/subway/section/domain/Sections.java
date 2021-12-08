package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<Section> sections;

    public Sections() {
		this.sections = new LinkedList<Section>();
	}

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        sections.add(new Section(line, upStation, downStation, distance));
    }

    public List<StationResponse> getStations() {
        List<Station> stations = this.sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        List<Station> downStations = this.sections.stream().map(Section::getDownStation).collect(Collectors.toList());

        int count = downStations.size();
        stations.add(downStations.get(count-1));
        return stations.stream()
		        .map(StationResponse::of)
		        .collect(Collectors.toList());
    }

}
