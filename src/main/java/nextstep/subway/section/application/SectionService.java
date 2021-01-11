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
		Line line = lineRepository.findById(lineId)
			.orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 데이터가 없습니다."));
 		Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 데이터가 없습니다."));
		Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 데이터가 없습니다."));
		line.addSection(new Section(line, upStation, downStation, sectionRequest.getDistance()));
	}

	private void validateSection(SectionRequest sectionRequest) {
		List<Station> stations = stationRepository.findAllById(
			Lists.newArrayList(sectionRequest.getUpStationId(), sectionRequest.getDownStationId()));
		List<Long> stationIds = stations.stream()
			.map(s -> s.getId())
			.collect(Collectors.toList());
		if (!stationIds.contains(sectionRequest.getUpStationId()) || !stationIds.contains(sectionRequest.getDownStationId())) {
			throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않습니다.");
		}
	}
}
