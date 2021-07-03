package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	public void add(Section section) {
		updateUpSection(section);
		updateDownSection(section);
		this.sections.add(section);
	}

	private void addDownSection(Section newSection, Section oldSection) {
		int distance = oldSection.getSubtractDistance(newSection);
		sections.add(
			new Section(newSection.getLine(), newSection.getDownStation(), oldSection.getDownStation(), distance));
		sections.remove(oldSection);
	}

	private void addUpSection(Section newSection, Section oldSection) {
		int distance = oldSection.getSubtractDistance(newSection);
		sections.add(
			new Section(newSection.getLine(), newSection.getDownStation(), oldSection.getDownStation(), distance));
		sections.remove(oldSection);
	}

	private void updateUpSection(Section newSection) {
		this.sections.stream()
			.filter(oldSection -> newSection.getUpStation() == oldSection.getUpStation())
			.findFirst()
			.ifPresent(oldSection -> addUpSection(newSection, oldSection));
	}

	private void updateDownSection(Section newSection) {
		this.sections.stream()
			.filter(oldSection -> newSection.getDownStation() == oldSection.getDownStation())
			.findFirst()
			.ifPresent(oldSection -> addDownSection(newSection, oldSection));
	}

	public List<Station> getStations() {
		List<Station> stations = new ArrayList<>();
		Optional<Section> firstStation = findUpSection();

		while (firstStation.isPresent()) {
			Section section = firstStation.get();
			stations.add(section.getDownStation());
			firstStation = findDownSection(section.getDownStation());
		}

		return stations;
	}

	public void addAll(Section upSection, Section downSection) {
		this.sections.add(upSection);
		this.sections.add(downSection);
	}

	public List<Section> getSections() {
		List<Section> stations = new ArrayList<>();
		Optional<Section> firstStation = findUpSection();

		while (firstStation.isPresent()) {
			Section section = firstStation.get();
			stations.add(section);
			firstStation = findDownSection(section.getDownStation());
		}

		return stations;
	}

	private Optional<Section> findUpSection() {
		Optional<Section> station = this.sections
			.stream().filter(section -> section.getUpStation() == null)
			.findFirst();
		return station;
	}

	private Optional<Section> findDownSection(Station downStation) {
		Optional<Section> station = this.sections
			.stream().filter(section -> section.getUpStation() == downStation)
			.findFirst();
		return station;
	}
}
