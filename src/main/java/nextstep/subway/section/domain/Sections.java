package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.InvalidSectionException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public void add(Section section) {
		checkExistBoth(section);
		checkNotExistBoth(section);
		// 상행선이 있을떄
		if (isExistUpSection(section)) {
			updateUpSection(section);
		}
		// 하행선이 있을때
		if (isExistDownSection(section)) {
			updateDownSection(section);
		}

		this.sections.add(section);
	}

	private void checkExistBoth(Section section) {
		boolean isExistUpStation = sections.stream()
			.anyMatch(sec -> sec.getUpStation() == section.getUpStation());
		boolean isExistDownStation = sections.stream()
			.anyMatch(sec -> sec.getDownStation() == section.getDownStation());

		if (isExistUpStation && isExistDownStation) {
			throw new InvalidSectionException("상행역과 하행역이 이미 노선에 등록되어 있습니다.");
		}
	}

	private void checkNotExistBoth(Section section) {
		boolean isNotExistUpStation = sections.stream()
			.noneMatch(
				sec -> sec.getUpStation() == section.getUpStation()
					|| sec.getUpStation() == section.getDownStation()
			);

		boolean isNotExistDownStation = sections.stream()
			.noneMatch(
				sec -> sec.getDownStation() == section.getDownStation()
					|| sec.getDownStation() == section.getUpStation()
			);

		if (isNotExistUpStation && isNotExistDownStation) {
			throw new InvalidSectionException("상행역과 하행역 둘다 포함되어있지 않습니다.");
		}
	}

	private boolean isExistUpSection(Section newSection) {
		return sections.stream()
			.anyMatch(section -> section.getUpStation() == newSection.getUpStation());
	}

	private boolean isExistDownSection(Section newSection) {
		return sections.stream()
			.anyMatch(section -> section.getDownStation() == newSection.getDownStation());
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

	private void addDownSection(Section newSection, Section oldSection) {
		if (oldSection.getUpStation() == null) {
			oldSection.updateDownStation(newSection.getUpStation());
			return;
		}

		int distance = oldSection.getSubtractDistance(newSection);
		sections.add(new Section(newSection.getLine(), newSection.getDownStation(), oldSection.getDownStation(), distance));
		sections.remove(oldSection);
	}

	private void addUpSection(Section newSection, Section oldSection) {
		int distance = oldSection.getSubtractDistance(newSection);
		sections.add(new Section(newSection.getLine(), newSection.getDownStation(), oldSection.getDownStation(), distance));
		sections.remove(oldSection);
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
		return this.sections.stream()
			.filter(section -> section.getUpStation() == null)
			.findFirst();
	}

	private Optional<Section> findDownSection(Station downStation) {
		return this.sections.stream()
			.filter(section -> section.getUpStation() == downStation)
			.findFirst();
	}

	public void addAll(Section upSection, Section downSection) {
		this.sections.add(upSection);
		this.sections.add(downSection);
	}
}
