package nextstep.subway.line.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineAddRequest;
import nextstep.subway.line.dto.LineEditRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionAddRequest;
import nextstep.subway.line.exception.LineNotFoundException;
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

	public LineResponse saveLine(LineAddRequest request) {
		final Map<Long, Station> stations = stationService.getStationsIn(request.getUpStationId(),
			request.getDownStationId());
		final Station upStation = stations.get(request.getUpStationId());
		final Station downStation = stations.get(request.getDownStationId());
		final Line line = lineRepository.save(
			Line.of(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));
		return LineResponse.of(line);
	}

	public List<LineResponse> getLines() {
		return lineRepository.findAll().stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse getLine(Long id) {
		return LineResponse.of(findLine(id));
	}

	public void updateLine(Long id, LineEditRequest request) {
		final Line line = findLine(id);
		line.update(request.toLine());
	}

	public void deleteLine(Long id) {
		try {
			lineRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new LineNotFoundException();
		}
	}

	private Line findLine(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(LineNotFoundException::new);
	}

	public Long saveSection(Long lineId, SectionAddRequest request) {
		final Map<Long, Station> stations = stationService.getStationsIn(request.getUpStationId(),
			request.getDownStationId());
		final Station upStation = stations.get(request.getUpStationId());
		final Station downStation = stations.get(request.getDownStationId());
		final Line line = findLine(lineId);
		line.addSection(upStation, downStation, request.getDistance());
		final Line savedLine = lineRepository.save(line); // @note: insert the new section, but not update(update when commit transaction)
		return savedLine.findSectionBy(upStation, downStation).getId();
	}
}
