package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;

@Service
@Transactional(readOnly = true)
public class LineService {
	private final StationRepository stationRepository;
	private final LineRepository lineRepository;

	public LineService(StationRepository stationRepository, LineRepository lineRepository) {
		this.stationRepository = stationRepository;
		this.lineRepository = lineRepository;
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest, Section section) {
		Line line = lineRepository.save(lineRequest.toLine(section));
		return LineResponse.of(line);
	}

	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();

		return lines.stream().map(line -> LineResponse.of(line)).collect(Collectors.toList());
	}

	public LineResponse findLine(Long lineId) {
		Line line = lineRepository.findById(lineId).orElseThrow(() -> new LineNotFoundException());
		return LineResponse.of(line);
	}

	@Transactional
	public void updateNameAndColor(Long lineId, LineRequest lineRequest) {
		Line line = lineRepository.getById(lineId);
		line.updateNameAndColor(lineRequest.getName(), lineRequest.getColor());
	}

	@Transactional
	public void deleteLine(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void addSection(Long lineId, Section section) {
		Line line = lineRepository.getById(lineId);
		line.add(section);
	}

	@Transactional
	public void removeSectionByStationId(Long lineId, Long stationId) {
		Line line = lineRepository.findById(lineId).orElseThrow(() -> new LineNotFoundException());
		Station removeStation = stationRepository.findById(stationId).orElseThrow(() -> new StationNotFoundException());

		line.removeSection(removeStation);
	}

}
