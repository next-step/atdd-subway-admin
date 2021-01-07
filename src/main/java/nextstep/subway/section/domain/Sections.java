package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nextstep.subway.common.exception.ExistException;
import nextstep.subway.common.exception.NothingException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class Sections {
	public static final int SECTION_DELETE_MINIMUM_COUNT = 1;

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	public Sections(Section section) {
		this.sections.add(section);
	}

	public static Sections of(Line line, Station upStation, Station downStation, int distance) {
		return new Sections(new Section(line, upStation, downStation, distance));
	}

	public void add(Section target) {
		validateAddSection(target);

		if (isExistStation(target.getUpStation())) {
			this.sections.stream()
				.filter(section -> section.isUpStation(target))
				.findFirst()
				.ifPresent(section -> section.updateUpStation(target));
			this.sections.add(target);
			return;
		}

		if (isExistStation(target.getDownStation())) {
			this.sections.stream()
				.filter(section -> section.isDownStation(target))
				.findFirst()
				.ifPresent(section -> section.updateDownStation(target));
			this.sections.add(target);
		}
	}

	public void delete(Station target) {
		validateDeleteSection(target);

	}

	public boolean isExistStation(Station station) {
		return this.sections.stream()
			.anyMatch(section -> section.contains(station));
	}

	private void validateAddSection(Section target) {
		boolean upStationExist = isExistStation(target.getUpStation());
		boolean downStationExist = isExistStation(target.getDownStation());

		if (upStationExist && downStationExist) {
			throw new ExistException("이미 존재하는 구간입니다.");
		}

		if (!upStationExist && !downStationExist) {
			throw new NothingException("등록할 수 없는 구간입니다.");
		}
	}

	public List<Station> getStations() {
		Set<Station> result = new LinkedHashSet<>();
		for (Section section : this.sections) {
			result.addAll(section.getStations());
		}
		return new ArrayList<>(result);
	}

	private boolean isContainStation(Station target) {
		return this.sections.stream()
			.anyMatch(station -> station.contains(target));
	}

	private Section findUpSection(Station target) {
		return this.sections.stream()
			.filter(section -> section.getUpStation().equals(target))
			.findFirst()
			.orElse(null);
	}

	private Section findDownSection(Station target) {
		return this.sections.stream()
			.filter(section -> section.getDownStation().equals(target))
			.findFirst()
			.orElse(null);
	}
}
