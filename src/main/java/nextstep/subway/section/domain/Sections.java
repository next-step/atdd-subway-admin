package nextstep.subway.section.domain;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
	private List<Section> values = new ArrayList<>();

	protected Sections() {
	}

	private Sections(List<Section> values) {
		this.values = values;
	}

	public void add(Section section) {
		this.values.add(section);
	}

	public static Sections empty() {
		return new Sections(new ArrayList<>());
	}

	public List<Station> toStations() {
		List<Station> stations = this.values.stream()
			.map(Section::getUpStation)
			.collect(toList());

		Section lastSection = this.values.get(values.size() - 1);

		stations.add(lastSection.getDownStation());

		return stations;
	}
}
