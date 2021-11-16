package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class LineService {

	private final LineRepository lineRepository;
	private final StationService stationService;
	private final SectionService sectionService;

	public LineService(LineRepository lineRepository, StationService stationService, SectionService sectionService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
		this.sectionService = sectionService;
	}

	public LineResponse saveLine(LineRequest request) {
		final Line line = request.toLine();
		line.add(getTerminalSections(request.getUpStationId(), request.getDownStationId(), request.getDistance()));
		return LineResponse.of(lineRepository.save(line));
	}

	private List<Section> getTerminalSections(Long upStationId, Long downStationId, int distance) {
		final Station upStation = stationService.getStation(upStationId);
		final Station downStation = stationService.getStation(downStationId);
		return sectionService.getTerminalSections(upStation, downStation, distance);
	}

	public List<LineResponse> getLines() {
		return lineRepository.findAll().stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse getLine(Long id) {
		return LineResponse.of(findLine(id));
	}

	public void updateLine(Long id, LineRequest request) {
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
}
