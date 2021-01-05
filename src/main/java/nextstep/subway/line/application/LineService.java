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
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;

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
		Line persistLine = findLineById(id);
		return LineResponse.of(persistLine);
	}

	public LineResponse updateLine(Long id, LineRequest request) {
		Line line = findLineById(id);
		line.update(request.toLine());
		return LineResponse.of(line);
	}

	public void deleteLine(Long id) {
		Line line = findLineById(id);
		lineRepository.delete(line);
	}

	public LineResponse addSection(Long id, SectionRequest sectionRequest) {
		Station upStation = findStationById(sectionRequest.getUpStationId());
		Station downStation = findStationById(sectionRequest.getDownStationId());
		Line persistLine = findLineById(id);
		persistLine.addSection(upStation, downStation, sectionRequest.getDistance());
		return LineResponse.of(persistLine);
	}

	private Line findLineById(Long id) {
		return lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
	}

	private Station findStationById(Long stationId) {
		return stationRepository.findById(stationId).orElseThrow(StationNotFoundException::new);
	}

}
