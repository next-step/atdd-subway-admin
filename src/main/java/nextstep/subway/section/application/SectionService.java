package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

@Service
public class SectionService {

	private final SectionRepository sectionRepository;
	private final StationService stationService;

	public SectionService(SectionRepository sectionRepository,
		  StationService stationService) {
		this.sectionRepository = sectionRepository;
		this.stationService = stationService;
	}

	public Section save(Line line, LineRequest lineRequest) {
		Station upStation = stationService.findById(lineRequest.getUpStationId());
		Station downStation = stationService.findById(lineRequest.getDownStationId());

		return sectionRepository.save(new Section(line, upStation, downStation, lineRequest.getDistance()));
	}
}
