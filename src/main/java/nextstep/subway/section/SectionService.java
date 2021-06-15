package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SectionService {
	private final LineRepository lineRepository;
	private final StationService stationService;

	public SectionService(LineRepository lineRepository, StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	public void addSection(Long lineId, SectionRequest sectionRequest) {
		Line line = lineRepository.findById(lineId)
				.orElseThrow(() -> new LineNotFoundException(lineId));
		addSectionToLine(line, sectionRequest.getStationIds(), sectionRequest.getDistance());
	}

	public void addSectionToLine(Line line, List<Long> stationsIds, Integer distance) {
		Station upStation = stationService.findById(stationsIds.get(0));
		Station downStation = stationService.findById(stationsIds.get(1));
		Section section = new Section(upStation, downStation, distance);
		section.toLine(line);
	}
}
