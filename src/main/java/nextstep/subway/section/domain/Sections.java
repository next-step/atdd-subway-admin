package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Getter;

@Getter
@Embeddable
public class Sections {
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "line_id")
	private List<Section> sections = new ArrayList<>();

	public void addInitSection(Section section) {
		sections.add(section);
	}

	public void addNewSection(Section section) {
		validateSection(section);
		sections.stream()
			.filter(s -> s.isSameUpStationId(section.getUpStation()))
			.findFirst()
			.ifPresent(s -> s.updateUpStation(section.getDownStation(), section.getDistance()));
		sections.stream()
			.filter(s -> s.isSameDownStationId(section.getDownStation()))
			.findFirst()
			.ifPresent(s -> s.updateDownStation(section.getUpStation(), section.getDistance()));
		sections.add(section);
	}

	private void validateSection(Section section) {
		boolean isBothExist = sections.stream()
			.anyMatch(s -> s.isSameUpStationId(section.getUpStation())
				&& s.isSameDownStationId(section.getDownStation()));
		if (isBothExist) {
			throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
		}
		sections.stream()
			.filter(s -> s.isSameUpStationId(section.getUpStation())
				|| s.isSameDownStationId(section.getDownStation())
				|| s.isSameUpStationId(section.getDownStation())
				|| s.isSameDownStationId(section.getUpStation()))
			.findFirst().orElseThrow(() -> new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다."));
	}
}
