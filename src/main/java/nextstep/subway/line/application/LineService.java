package nextstep.subway.line.application;

import nextstep.subway.exception.ApiException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
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
		Line persistLine = lineRepository.save(createLine(request));
		return LineResponse.of(persistLine);
	}

	private Line createLine(final LineRequest request) {
		Station upStation = findStationById(request.getUpStationId());
		Station downStation = findStationById(request.getDownStationId());

		Line line = request.toLine();
		line.addSection(new Section(upStation, downStation, request.getDistance()));
		return line;
	}

	private Station findStationById(final Long id) {
		return stationRepository.findById(id).orElseThrow(() -> new ApiException(NOT_FOUND_STATION));
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

	@Transactional(readOnly = true)
	public LineResponse getLine(final Long id) {
		return lineRepository.findById(id)
							 .map(LineResponse::of)
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
}
