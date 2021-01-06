package nextstep.subway.line.application;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;

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
		//Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
		//Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
		line.addSection(new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
			sectionRequest.getDistance()));
	}
}
