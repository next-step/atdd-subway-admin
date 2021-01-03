package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@AllArgsConstructor
@Service
@Transactional
public class LineService {
	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public LineResponse saveLine(LineRequest request) {
		Station upStation = findStationById(request.getUpStationId());
		Station downStation = findStationById(request.getDownStationId());
		Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
		return LineResponse.of(persistLine);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> showLines() {
		List<Line> lines = lineRepository.findAll();
		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse showLine(Long id) {
		Line persistLine = findLine(id);
		return LineResponse.of(persistLine);
	}

	public LineResponse updateLine(Long id, LineRequest request) {
		Line line = findLine(id);
		line.update(request.toLine());
		return LineResponse.of(line);
	}

	public void deleteLine(Long id) {
		Line line = findLine(id);
		lineRepository.delete(line);
	}

	private Line findLine(Long id) {
		return lineRepository.findById(id).orElseThrow(RuntimeException::new);
	}

	private Station findStationById(String stationId) {
		return stationRepository.findById(Long.parseLong(stationId)).orElseThrow(RuntimeException::new);
	}
}
