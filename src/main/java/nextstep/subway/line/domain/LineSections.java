package nextstep.subway.line.domain;

import nextstep.subway.line.application.SectionValidationException;
import nextstep.subway.station.domain.Station;
import org.springframework.lang.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class LineSections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections;

	public LineSections() {
	}

	public LineSections(Section... sections) {
		this.sections = new ArrayList<>(Arrays.asList(sections));
	}

	public List<Station> getSortedStations() {
		Optional<Section> section = findFirstSection();
		if (!section.isPresent()) {
			return Collections.emptyList();
		}

		List<Station> stations = new ArrayList<>();
		stations.add(section.get().getFront());
		while (section.isPresent()) {
			stations.add(section.get().getBack());
			section = findSectionByFrontStation(sections, section.get().getBack());
		}

		return stations;
	}

	private Optional<Section> findFirstSection() {
		Optional<Section> nextSection = this.sections.stream().findAny();
		Optional<Section> firstSection = nextSection;
		while (nextSection.isPresent()) {
			firstSection = nextSection;
			nextSection = findSectionByBackStation(this.sections, nextSection.get().getFront());
		}

		return firstSection;
	}

	private static Optional<Section> findSectionByFrontStation(List<Section> sections, Station station) {
		return sections.stream()
				.filter(section -> section.isFrontEqual(station))
				.findFirst();
	}

	private static Optional<Section> findSectionByBackStation(List<Section> sections, Station station) {
		return sections.stream()
				.filter(section -> section.isBackEqual(station))
				.findFirst();
	}

	public void addSection(Section section) {
		validateAddSection(section);
		Optional<Section> splitTarget = this.sections.stream()
				.filter(s -> s.isCanSplit(section))
				.findFirst();

		if (splitTarget.isPresent()) {
			List<Section> split = splitTarget.get().splitSection(section);
			this.sections.remove(splitTarget.get());
			this.sections.addAll(split);
			return;
		}

		this.sections.add(section);
	}

	private void validateAddSection(Section section) throws SectionValidationException {
		final boolean hasSameStations = this.sections.stream().anyMatch(section::containsAllStation);
		if (hasSameStations) {
			throw new SectionValidationException("same stations already added");
		}

		final boolean hasNoStations = this.sections.stream().noneMatch(section::containsAnyStation);
		if (hasNoStations) {
			throw new SectionValidationException("one station must be included in sections");
		}
	}
}
