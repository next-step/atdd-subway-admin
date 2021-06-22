package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class LineService {
	private static final int ADJUST_NEXT_INDEX = 1;

	private StationService stationService;
	private SectionService sectionService;
	private LineRepository lineRepository;

	public LineService(StationService stationService, SectionService sectionService, LineRepository lineRepository) {
		this.stationService = stationService;
		this.sectionService = sectionService;
		this.lineRepository = lineRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		Station upStation = stationService.findStationByIdFromRepository(request.getUpStationId());
		Station downStation = stationService.findStationByIdFromRepository(request.getDownStationId());
		Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
		return LineResponse.of(persistLine);
	}

	private LineResponse changeSortedStations(Line persistLine) {
		return LineResponse.of(persistLine);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		return lineRepository.findAll().stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findLineById(Long id) {
		Line persistLine = findLineByIdFromRepository(id);
		return changeSortedStations(persistLine);
	}

	private Line findLineByIdFromRepository(Long id) {
		return Optional.ofNullable(lineRepository.findById(id)).get()
			.orElseThrow(new NotFoundException("지하철 노선을 찾을 수 없습니다. id :" + id));
	}

	public void deleteLineById(Long id) {
		sectionService.deleteAllByLine(findLineByIdFromRepository(id));
		lineRepository.deleteById(id);
	}

	public void updateLine(Long id, LineRequest lineRequest) {
		Line sourceLine = findLineByIdFromRepository(id);
		sourceLine.update(lineRequest.toLine());
	}
}
