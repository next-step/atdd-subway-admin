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
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;

@Service
@Transactional
public class LineService {
	private LineRepository lineRepository;

	public LineService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		Line persistLine = lineRepository.save(request.toLine());
		return LineResponse.of(persistLine);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();
		return LineResponse.ofList(lines);
	}

	@Transactional(readOnly = true)
	public LineResponse findLineById(Long id) {
		return LineResponse.of(findLineByIdFromRepository(id));
	}

	private Line findLineByIdFromRepository(Long id) {
		return Optional.ofNullable(lineRepository.findById(id)).get()
			.orElseThrow(new NotFoundException("지하철 노선을 찾을 수 없습니다. id :" + id));
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	public void updateLine(Long id, LineRequest lineRequest) {
		Line sourceLine = findLineByIdFromRepository(id);
		sourceLine.update(lineRequest.toLine());
	}
}
