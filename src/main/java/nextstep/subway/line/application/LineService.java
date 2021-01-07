package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import nextstep.subway.common.exception.NothingException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
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
		Line persistLine = findLineById(id);
		return LineResponse.of(persistLine);
	}

	@Transactional
	public LineResponse updateLine(Long id, LineRequest request) {
		Line line = findLineById(id);
		line.update(request.toLine());
		return LineResponse.of(line);
	}

	@Transactional
	public void deleteLine(Long id) {
		Line line = findLineById(id);
		lineRepository.delete(line);
	}

	@Transactional
	public LineResponse addSection(Long id, SectionRequest sectionRequest) {
		Station upStation = findStationById(sectionRequest.getUpStationId());
		Station downStation = findStationById(sectionRequest.getDownStationId());
		Line line = findLineById(id);
		line.addSection(upStation, downStation, sectionRequest.getDistance());
		Line persistLine = lineRepository.save(line);
		return LineResponse.of(persistLine);
	}

	@Transactional
	public void deleteSection(Long id, Long stationId) {
		Station station = findStationById(stationId);
		Line line = findLineById(id);
		line.deleteSection(station);
	}

	private Line findLineById(Long id) {
		return lineRepository.findById(id).orElseThrow(NothingException::new);
	}

	private Station findStationById(Long stationId) {
		return stationRepository.findById(stationId).orElseThrow(NothingException::new);
	}
}
