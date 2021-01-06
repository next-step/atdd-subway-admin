package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

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
		Line persistLine = lineRepository.save(lineRequestToLineWithSection(request));
		return LineResponse.of(persistLine);
	}

	private Line lineRequestToLineWithSection(LineRequest request) {
		Station upStation = stationService.findStationById(request.getUpStationId());
		Station downStation = stationService.findStationById(request.getDownStationId());
		return new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance());
	}

	public List<LineResponse> findAllLines() {
		List<Line> persistLine = lineRepository.findAll();
		return persistLine.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse findLine(Long id) {
		Line persistLine = getLineById(id);

		List<StationResponse> stationResponses = stationService.findAllById(persistLine.getStationsIds()).stream()
			.map(station -> new StationResponse(station.getId(), station.getName(), station.getCreatedDate(),
				station.getModifiedDate()))
			.collect(Collectors.toList());

		return LineResponse.of(persistLine, stationResponses);
	}

	public void updateLine(Long id, LineRequest lineRequest) {
		Line persistLine = getLineById(id);
		persistLine.update(lineRequest.toLine());
	}

	public Line getLineById(Long id) {
		return lineRepository.findById(id).orElseThrow(() -> new NotFoundException());
	}

	public void deleteLine(Long id) {
		lineRepository.deleteById(id);
	}

}
