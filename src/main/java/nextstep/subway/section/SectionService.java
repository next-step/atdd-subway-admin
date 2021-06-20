package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.exception.InvalidSectionException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SectionService {
	private static final int UP_STATION_INDEX = 0;
	private static final int DOWN_STATION_INDEX = 1;

	private final LineRepository lineRepository;
	private final StationService stationService;

	public SectionService(LineRepository lineRepository, StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	public void addSection(Long lineId, SectionRequest sectionRequest) {
		Line line = lineRepository.findById(lineId)
				.orElseThrow(() -> new LineNotFoundException(lineId));
		validateSection(line.getStationsInSections(), sectionRequest.getStationIds());
		addSectionToLine(line, sectionRequest.getStationIds(), sectionRequest.getDistance());
	}

	public void addSectionToLine(Line line, List<Long> stationsIds, Integer distance) {
		Station upStation = stationService.findById(stationsIds.get(UP_STATION_INDEX));
		Station downStation = stationService.findById(stationsIds.get(DOWN_STATION_INDEX));
		Section section = new Section(upStation, downStation, distance);
		section.toLine(line);
	}

	private void validateSection(List<Station> stations, List<Long> stationsIdsToAdd) {
		boolean alreadyExists = stations.stream().map(Station::getId)
				.anyMatch(stationsIdsToAdd::contains);

		if (!alreadyExists) {
			throw new InvalidSectionException();
		}
	}

	public void removeSectionByStationId(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId)
				.orElseThrow(() -> new LineNotFoundException(lineId));
		line.deleteSectionByStationId(stationId);
	}
}
