package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.line.exception.SectionNotFoundException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class LineSections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
	private List<LineSection> lineSections = new ArrayList<>();

	public void add(Line line, List<Section> sections) {
		lineSections.addAll(
			sections.stream().map(section -> new LineSection(line, section))
				.collect(Collectors.toList())
		);
	}

	public List<Station> getStationsFromUpTerminal() {
		final Section upTerminalSection = getUpTerminalSection();
		return sortSectionsByUpToDownFrom(upTerminalSection).stream()
			.map(Section::getDownStation)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	private Section getUpTerminalSection() {
		return lineSections.stream().filter(LineSection::isUpTerminal)
			.findAny()
			.orElseThrow(SectionNotFoundException::new)
			.getSection();
	}

	private List<Section> sortSectionsByUpToDownFrom(Section firstSection) {
		final Map<Station, Section> sectionByUpStation = toSectionMap(lineSection -> lineSection.getSection().getUpStation());
		final List<Section> sections = new LinkedList<>();
		Section section = firstSection;
		while (null != section && !section.isDownTerminal()) {
			sections.add(section);
			section = sectionByUpStation.get(section.getDownStation());
		}
		sections.add(section);
		return sections;
	}

	private <K> Map<K, Section> toSectionMap(Function<? super LineSection, ? extends K> keyMapper) {
		return lineSections.stream()
			.collect(Collectors.toMap(keyMapper, LineSection::getSection));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof LineSections))
			return false;
		LineSections that = (LineSections)o;
		return Objects.equals(lineSections, that.lineSections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(lineSections);
	}
}
