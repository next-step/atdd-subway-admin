package nextstep.subway.section.application;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationGroup;

@Service
@Transactional
public class SectionService {
	private StationService stationService;
	private SectionRepository sectionRepository;

	public SectionService(StationService stationService, SectionRepository sectionRepository) {
		this.stationService = stationService;
		this.sectionRepository = sectionRepository;
	}

	public Section saveSection(LineRequest request, Line persistLine) {
		Station upStation = stationService.findStationByIdFromRepository(Long.parseLong(request.getUpStationId()));
		Station downStation = stationService.findStationByIdFromRepository(Long.parseLong(request.getDownStationId()));
		Section section = Section.generate(persistLine, upStation, downStation, request.getDistance());
		return sectionRepository.save(section);
	}

	public void deleteAllByLine(Line line) {
		sectionRepository.deleteByLine(line);
	}

	public Section findSectionById(Long id) {
		return Optional.ofNullable(sectionRepository.findById(id)).get()
			.orElseThrow(new NotFoundException("섹션을 찾을 수 없습니다. id :" + id));
	}

	public StationGroup findSortedStationsByLine(Line line) {
		StationGroup totalStations = sectionRepository.findAllByLine(line).stream()
			.map(Section::upStation)
			.collect(Collectors.collectingAndThen(Collectors.toList(), StationGroup::new));
		StationGroup downStations = sectionRepository.findAllByLine(line).stream()
			.map(Section::downStation)
			.collect(Collectors.collectingAndThen(Collectors.toList(), StationGroup::new));
		totalStations.addStationGroup(downStations);
		return totalStations;
	}
}
