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
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class LineService {
	private StationService stationService;
	private LineRepository lineRepository;

	public LineService(StationService stationService, LineRepository lineRepository) {
		this.stationService = stationService;
		this.lineRepository = lineRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		Station upStation = stationService.findStationByIdFromRepository(request.getUpStationId());
		Station downStation = stationService.findStationByIdFromRepository(request.getDownStationId());
		Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
		return LineResponse.of(persistLine);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		return findAllLinesFromRepository().stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	private List<Line> findAllLinesFromRepository() {
		List<Line> lines = lineRepository.findAll();
		lines.forEach(line -> line.sectionGroup().sort());
		return lines;
	}

	@Transactional(readOnly = true)
	public LineResponse findLineById(Long id) {
		Line persistLine = findLineByIdFromRepository(id);
		return LineResponse.of(persistLine);
	}

	private Line findLineByIdFromRepository(Long id) {
		return Optional.ofNullable(lineRepository.findById(id))
			.get()
			.map(line -> {
				line.sectionGroup().sort();
				return line;
			})
			.orElseThrow(new NotFoundException("지하철 노선을 찾을 수 없습니다. id :" + id));
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public void updateLine(Long id, LineRequest lineRequest) {
		Line sourceLine = findLineByIdFromRepository(id);
		sourceLine.update(lineRequest.toLine());
	}

	public void addSection(Long lineId, SectionRequest sectionRequest) {
		Line line = findLineByIdFromRepository(lineId);
		Station upStation = stationService.findStationByIdFromRepository(sectionRequest.getUpStationId());
		Station downStation = stationService.findStationByIdFromRepository(sectionRequest.getDownStationId());
		line.addSection(sectionRequest.toSection(line, upStation, downStation));
		lineRepository.save(line);
	}

	public void removeSectionByStationId(Long lineId, Long stationId) {
		Line line = findLineByIdFromRepository(lineId);
		Station removeTargetStation = stationService.findStationByIdFromRepository(stationId);
		line.sectionGroup().removeSectionByStation(removeTargetStation);
		lineRepository.save(line);
	}
}
