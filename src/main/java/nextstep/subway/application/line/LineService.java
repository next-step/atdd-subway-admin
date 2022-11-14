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
import nextstep.subway.dto.line.LineUpdateRequest;
import nextstep.subway.exception.LineNotFoundException;

@Service
@Transactional(readOnly = true)
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

	@Transactional
	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public List<LineResponse> findLines() {
		return lineRepository.findAll().stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse findLine(Long id) {
		return LineResponse.of(findById(id));
	}

	@Transactional
	public void updateLine(Long id, LineUpdateRequest request) {
		Line line = findById(id);
		line.updateLine(request);
	}

	private Line findById(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(() -> new LineNotFoundException(LINE_NOT_FOUND_MESSAGE + id));
	}

}
