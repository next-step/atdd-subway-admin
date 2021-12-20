package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public void addSection(Section section) {
		checkValidation(section);

		final Optional<Section> targetSection = sections.stream()
			.filter(it -> it.isSameUpstation(section) || it.isSameDownStation(section))
			.findFirst();

		targetSection
			.filter(it -> it.isSameUpstation(section))
			.ifPresent(it -> it.setUpStation(section.getDownStation()));

		targetSection
			.filter(it -> it.isSameDownStation(section))
			.ifPresent(it -> it.setDownStation(section.getUpStation()));

		sections.add(section);
	}

	private void checkValidation(Section section) {
		if (sections.stream().anyMatch(it -> it.isSameStation(section))) {
			throw new IllegalArgumentException("이미 존재하는 구간입니다.");
		}

		if (sections.stream()
			.filter(
				it -> it.isSameUpstation(section) || it.isSameDownStation(section))
			.anyMatch(it -> it.diffDistance(section) <= 0)) { // 추가하려는 distance가 같거나 더 크면
			throw new IllegalArgumentException("distance가 같거나 더 큽니다");
		}

		final Set<Station> stations = sections.stream()
			.flatMap(it -> Stream.of(it.getUpStation(), it.getDownStation()))
			.collect(Collectors.toSet());

		if (stations.size() > 0 && !(stations.contains(section.getUpStation()) || stations.contains(
			section.getDownStation()))) {
			throw new IllegalArgumentException("존재하지 않는 역입니다.");
		}
	}

	public List<Station> getStations() {
		final Set<Station> downStations = sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toSet());

		Optional<Section> nextSection = sections.stream()
			.filter(it -> !downStations.contains(it.getUpStation()))
			.findFirst();

		List<Station> result = new ArrayList<>();
		while (nextSection.isPresent()) {
			Section section = nextSection.get();
			result.add(section.getUpStation());
			nextSection = sections.stream()
				.filter(it -> Objects.equals(it.getUpStation(), section.getDownStation()))
				.findFirst();
			if (!nextSection.isPresent()) {
				result.add(section.getDownStation());
			}
		}

		return result;
	}

	public boolean contains(Section section) {
		return this.sections.contains(section);
	}
}
