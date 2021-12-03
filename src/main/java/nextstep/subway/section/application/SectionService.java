package nextstep.subway.section.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class SectionService {

	private final SectionRepository sectionRepository;
	private final StationService stationService;

	public SectionService(SectionRepository sectionRepository,
		StationService stationService) {
		this.sectionRepository = sectionRepository;
		this.stationService = stationService;
	}

	public Section saveSection(SectionRequest request) {
		Station upStation = stationService.findById(request.getUpStationId());
		Station downStation = stationService.findById(request.getDownStationId());
		Section section = Section.of(upStation, downStation, request.getDistance());
		return sectionRepository.save(section);
	}

}
