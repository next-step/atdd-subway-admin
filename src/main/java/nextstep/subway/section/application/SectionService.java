package nextstep.subway.section.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class SectionService {
	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public void saveSection(Long lineId, SectionRequest sectionRequest) {
		validateSection(sectionRequest);
		Line line = getLine(lineId);
 		Station upStation = getStation(sectionRequest.getUpStationId());
		Station downStation = getStation(sectionRequest.getDownStationId());
		Section section = Section.builder()
			.line(line)
			.upStation(upStation)
			.downStation(downStation)
			.distance(sectionRequest.getDistance())
			.build();
		line.addNewSection(section);
	}

	private Line getLine(Long lineId) {
		return lineRepository.findById(lineId)
			.orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 데이터가 없습니다."));
	}

	private Station getStation(Long stationId) {
		return stationRepository.findById(stationId)
			.orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 데이터가 없습니다."));
	}

	private void validateSection(SectionRequest sectionRequest) {
		List<Station> stations = stationRepository.findAllById(
			Lists.newArrayList(sectionRequest.getUpStationId(), sectionRequest.getDownStationId()));
		List<Long> stationIds = stations.stream()
			.map(s -> s.getId())
			.collect(Collectors.toList());
		if (!stationIds.contains(sectionRequest.getUpStationId()) || !stationIds.contains(sectionRequest.getDownStationId())) {
			throw new IllegalArgumentException("등록되어 있지 않은 역입니다.");
		}
	}

	public void removeSectionByStationId(Long lineId, Long stationId) {
		Line line = getLine(lineId);
		Station station = getStation(stationId);
		line.removeSection(station);
	}
}
