package nextstep.subway.line.application;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

/**
 * @author : byungkyu
 * @date : 2021/01/06
 * @description :
 **/
@Service
@Transactional
public class SectionService {

	private final LineService lineService;
	private final StationService stationService;

	public SectionService(LineService lineService, StationService stationService) {
		this.lineService = lineService;
		this.stationService = stationService;
	}

	public void addSection(Long lineId, SectionRequest sectionRequest) {
		Line line = lineService.getLineById(lineId);
		line.addSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
			sectionRequest.getDistance());
	}

	public void removeSectionByStationId(Long lineId, Long stationId) {
		Line line = lineService.getLineById(lineId);
		line.removeSectionByStationId(stationId);
	}
}
