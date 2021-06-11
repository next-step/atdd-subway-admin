package nextstep.subway.line.application;

import nextstep.subway.exception.ApiException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ApiExceptionMessge.NOT_FOUND_LINE;
import static nextstep.subway.exception.ApiExceptionMessge.NOT_FOUND_STATION;

@Service
@Transactional
public class LineService {

	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public LineResponse saveLine(final LineRequest request) {
		Line line = request.toLine();
		line.addSection(createSection(request.getUpStationId(), request.getDownStationId(), request.getDistance()));
		return LineResponse.of(lineRepository.save(line));
	}

	private Station findStationById(final Long id) {
		return stationRepository.findById(id)
								.orElseThrow(() -> new ApiException(NOT_FOUND_STATION));
	}

	@Transactional(readOnly = true)
	public List<LineResponse> getLineRepository() {
		return convertLineResponse(lineRepository.findAll());
	}

	private List<LineResponse> convertLineResponse(List<Line> lines) {
		return lines.stream()
					.map(LineResponse::of)
					.collect(Collectors.toList());
	}

	public LineResponse getLine(final Long id) {
		return LineResponse.of(findById(id));
	}

	@Transactional(readOnly = true)
	public Line findById(final Long id) {
		return lineRepository.findById(id)
							 .orElseThrow(() -> new ApiException(NOT_FOUND_LINE));
	}

	public LineResponse updateLine(final Long id, final LineRequest request) {
		Line line = lineRepository.findById(id)
								  .orElseThrow(() -> new ApiException(NOT_FOUND_LINE));
		line.update(request.toLine());
		return LineResponse.of(line);
	}

	public void deleteLine(final Long id) {
		lineRepository.deleteById(id);
	}

	public LineResponse registerSection(final Long id, final SectionRequest request) {
		Line line = findById(id);
		line.addSection(createSection(request.getUpStationId(), request.getDownStationId(), request.getDistance()));
		return LineResponse.of(line);
	}

	private Section createSection(final Long upStationId, final Long downStationId, final int distance) {
		Station upStation = findStationById(upStationId);
		Station downStation = findStationById(downStationId);
		return Section.of(upStation, downStation, distance);
	}
}
