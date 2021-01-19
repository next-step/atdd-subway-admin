package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	protected Sections() {
	}

	public Sections(Section... sections) {
		this.sections.addAll(Arrays.asList(sections));
	}

	public List<Section> getSections() {
		return sections;
	}

	public void addSection(final Section section) {
		validate(section);
		findByUpStation(section.getUp())
			.ifPresent(existsSection ->
				existsSection.update(section.getDown(), existsSection.getDown(),
					existsSection.getDistance().minus(section.getDistance())));

		findByDownStation(section.getDown())
			.ifPresent(existsSection ->
				existsSection.update(section.getDown(), existsSection.getDown(),
					section.getDistance().minus(existsSection.getDistance())));

		this.sections.add(section);
	}

	private void validate(final Section section) {
		List<Station> stations = this.getStations();
		if (stations.contains(section.getUp()) && stations.contains(section.getDown())) {
			throw new IllegalArgumentException("상행역과 하행역이 이미 존재합니다.");
		}
	}

	public Optional<Section> findByUpStation(final Station up) {
		return this.sections.stream()
			.filter(section -> section.getUp().equals(up))
			.findFirst();
	}

	public Optional<Section> findByDownStation(final Station down) {
		return this.sections.stream()
			.filter(section -> section.getDown().equals(down))
			.findFirst();
	}

	public List<Station> getStations() {
		return this.sections.stream()
			.flatMap(Section::getStations)
			.distinct()
			.collect(Collectors.toList());
	}
}
