package nextstep.subway.application.line;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.application.stations.StationService;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.line.LineCreateRequest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.exception.LineNotFoundException;

@Service
public class LineService {

	private static final String LINE_NOT_FOUND_MESSAGE = "해당 노선을 찾을 수 없습니다 : ";
	private final LineRepository lineRepository;
	private final StationService stationService;

	public LineService(LineRepository lineRepository, StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	@Transactional
	public LineResponse saveLine(LineCreateRequest request) {
		Station upStation = stationService.findById(request.getUpStationId());
		Station downStation = stationService.findById(request.getDownStationId());
		Line line = lineRepository.save(
			Line.of(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));
		return LineResponse.of(line);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAll() {
		return lineRepository.findAll().stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findLine(Long id) {
		return LineResponse.of(findById(id));
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

	private Line findById(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(() -> new LineNotFoundException(LINE_NOT_FOUND_MESSAGE + id));
	}

}
