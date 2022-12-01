package nextstep.subway.application.line;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.application.stations.StationService;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.line.LineCreateRequest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.section.SectionCreateRequest;
import nextstep.subway.dto.stations.StationNameResponse;
import nextstep.subway.exception.LineNotFoundException;

@Service
public class LineService {

	private static final String LINE_NOT_FOUND_MESSAGE = "해당 노선을 찾을 수 없습니다 : ";
	private final LineRepository lineRepository;
	private final StationService stationService;
	private final SectionService sectionService;

	public LineService(
		LineRepository lineRepository,
		StationService stationService,
		SectionService sectionService
	) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
		this.sectionService = sectionService;
	}

	@Transactional
	public LineResponse saveLine(LineCreateRequest request) {
		Station upStation = stationService.findById(request.getUpStationId());
		Station downStation = stationService.findById(request.getDownStationId());
		Line line = lineRepository.save(
			Line.of(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));
		return lineResponse(line);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAll() {
		return lineRepository.findAll().stream()
			.map(this::lineResponse)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findLine(Long id) {
		Line byId = findById(id);
		return lineResponse(byId);
	}

	@Transactional
	public void updateLine(Long id, String name, String color) {
		Line line = findById(id);
		line.updateLine(name, color);
	}

	@Transactional
	public void deleteLine(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public LineResponse addSection(Long lineId, SectionCreateRequest request) {
		Line line = findById(lineId);

		Station upStation = findUpStation(request.getUpStationId());
		Station downStation = findDownStation(request.getDownStationId());
		List<Section> sectionsToUpdate = sectionService.findSectionsToUpdate(upStation, downStation);

		Section section = new Section(line, upStation , downStation, request.getDistance());
		line.connectSection(section, sectionsToUpdate);

		return lineResponse(line);
	}

	@Transactional
	public void removeSection(Long lineId, Long stationId) {
		Station station = stationService.findById(stationId);
		Line line = findById(lineId);
		line.removeSection(station);
	}

	private Line findById(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(() -> new LineNotFoundException(LINE_NOT_FOUND_MESSAGE + id));
	}

	private LineResponse lineResponse(Line line) {
		return LineResponse.of(
			line.getId(),
			line.getName(),
			line.getColor(),
			stationsNameResponses(line)
		);
	}

	private List<StationNameResponse> stationsNameResponses(Line line) {
		return line.allStations().stream()
			.map(station -> new StationNameResponse(station.getId(), station.getName()))
			.collect(Collectors.toList());
	}

	private Station findUpStation(Long upStationId) {
		return stationService.findById(upStationId);
	}

	private Station findDownStation(Long downStationId) {
		return stationService.findById(downStationId);
	}
}
