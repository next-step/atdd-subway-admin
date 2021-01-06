package nextstep.subway.section.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class SectionService {
	private final SectionRepository sections;
	private final StationRepository stations;

	public SectionService(SectionRepository sections, StationRepository stations) {
		this.sections = sections;
		this.stations = stations;
	}

	public Section saveSection(Line line, Long upStationId, Long downStationId, int distance) {
		Station upStation = getStation(upStationId);
		Station downStation = getStation(downStationId);
		return sections.save(new Section(line, upStation, downStation, distance));
	}

	private Station getStation(Long stationId) {
		return stations.findById(stationId)
			.orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 데이터가 없습니다."));
	}
}
