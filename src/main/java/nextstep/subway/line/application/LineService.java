package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class LineService {
	private LineRepository lineRepository;
	private StationService stationService;

	public LineService(LineRepository lineRepository, StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	public LineResponse saveLine(LineRequest request) {
		Station upStation = stationService.findById(request.getUpStationId());
		Station downStation = stationService.findById(request.getDownStationId());
		Line persistLine = lineRepository.save(Line.of(request.getName(), request.getColor(), upStation, downStation,
			request.getDistance()));
		return LineResponse.of(persistLine);
	}

	public List<LineResponse> findAllLines() {
		final List<Line> lines = lineRepository.findAll();
		return lines.stream().map(it -> LineResponse.of(it)).collect(Collectors.toList());
	}

	public LineResponse findLine(Long id) {
		return LineResponse.of(lineRepository.findById(id).get());
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}
}
