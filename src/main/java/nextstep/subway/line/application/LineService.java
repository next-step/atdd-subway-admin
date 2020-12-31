package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.AddSectionRequest;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationNotFoundException;
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
	public LineResponse saveLine(LineCreateRequest lineCreateRequest) {
		Station front = stationService.findById(lineCreateRequest.getUpStationId());
		Station back = stationService.findById(lineCreateRequest.getDownStationId());
		Line line = new Line(lineCreateRequest.getName(),
				lineCreateRequest.getColor(),
				front,
				back,
				lineCreateRequest.getStationDistance());
		line = lineRepository.save(line);
		return LineResponse.of(line);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLine() {
		return lineRepository.findAll().stream()
				.map(LineResponse::of)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findLineById(Long id) {
		return LineResponse.of(findLine(id));
	}

	@Transactional
	public void updateLineById(Long id, LineUpdateRequest lineUpdateRequest) {
		Line line = findLine(id);
		line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
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

	@Transactional
	public void addSection(Long id, AddSectionRequest addSectionRequest) {
		Station upStation;
		Station downStation;
		try {
			upStation = stationService.findById(addSectionRequest.getUpStationId());
			downStation = stationService.findById(addSectionRequest.getDownStationId());
		} catch (StationNotFoundException e) {
			throw new SectionValidationException("station not found");
		}

		findLine(id).addSection(upStation, downStation, addSectionRequest.getDistance());
	}
}
