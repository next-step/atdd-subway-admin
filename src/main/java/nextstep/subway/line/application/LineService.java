package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.NewLineRequest;
import nextstep.subway.line.dto.NewLineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
	private final LineRepository lineRepository;
	private final StationService stationService;

	public LineService(LineRepository lineRepository, StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	@Transactional
	public LineResponse saveLine(LineRequest request) {
		Line persistLine = lineRepository.save(request.toLine());
		return LineResponse.of(persistLine);
	}

	@Transactional
	public NewLineResponse saveLine_new(NewLineRequest newLineRequest) {
		Station front = stationService.findById(newLineRequest.getUpStationId());
		Station back = stationService.findById(newLineRequest.getDownStationId());
		Line line = new Line(newLineRequest.getName(),
				newLineRequest.getColor(),
				front,
				back,
				newLineRequest.getStationDistance());
		line = lineRepository.save(line);
		return NewLineResponse.of(line);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLine() {
		return lineRepository.findAll().stream()
				.map(LineResponse::of)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<NewLineResponse> findAllLine_new () {
		return lineRepository.findAll().stream()
				.map(NewLineResponse::of)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findLineById(Long id) {
		return LineResponse.of(findLine(id));
	}

	@Transactional(readOnly = true)
	public NewLineResponse findLineById_new(Long id) {
		return NewLineResponse.of(findLine(id));
	}

	@Transactional
	public void updateLineById(Long id, LineRequest lineRequest) {
		Line line = findLine(id);
		line.update(lineRequest.toLine());
	}

	@Transactional
	public void deleteLineById(Long id) {
		Line line = findLine(id);
		lineRepository.delete(line);
	}

	private Line findLine(Long id) {
		return lineRepository.findById(id)
				.orElseThrow(() -> new LineNotFoundException("cannot find line"));
	}
}
