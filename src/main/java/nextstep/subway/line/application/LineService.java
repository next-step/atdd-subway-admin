package nextstep.subway.line.application;

import nextstep.subway.exception.ApiException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ApiExceptionMessge.NOT_FOUND_LINE;

@Service
@Transactional
public class LineService {

	private final LineRepository lineRepository;

	private final StationService stationService;
	private final SectionService sectionService;

	public LineService(final LineRepository lineRepository, final StationService stationService, final SectionService sectionService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
		this.sectionService = sectionService;
	}

	public LineResponse saveLine(final LineRequest request) {
		Station upStation = stationService.findStationById(request.getUpStationId());
		Station downStation = stationService.findStationById(request.getDownStationId());
		Line persistLine = lineRepository.save(request.toLine());
		sectionService.registerSection(persistLine, upStation, downStation, request.getDistance());
		return LineResponse.of(persistLine);
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
		Station upStation = stationService.findStationById(request.getUpStationId());
		Station downStation = stationService.findStationById(request.getDownStationId());

		sectionService.registerSection(line, upStation, downStation, request.getDistance());
		return LineResponse.of(line);
	}
}
