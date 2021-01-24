package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class LineService {
	private final LineRepository lineRepository;
	private final StationService stationService;

	public LineService(LineRepository lineRepository, StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	public LineResponse saveLine(LineRequest request) {
		Map<Long, Station> stations = findStations(request);
		Station upStation = stations.get(request.getUpStationId());
		Station downStation = stations.get(request.getDownStationId());

		Line line = request.toLine();
		line.addStation(upStation, downStation, request.getDistance());

		return LineResponse.of(lineRepository.save(line));
	}

	private Map<Long, Station> findStations(LineRequest request){
		return stationService.findStationsByIds(Arrays.asList(request.getUpStationId(), request.getDownStationId()))
			.stream()
			.collect(Collectors.toMap(Station::getId, Function.identity()));
	}

	public LineResponse updateLine(Long id, LineRequest request) {
		Line line = findById(id);
		line.update(request.toLine());

		return LineResponse.of(line);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();

		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findLineById(Long id) {
		Line line = findById(id);

		return LineResponse.of(line);
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	private Line findById(Long id) {
		return lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
	}
}
