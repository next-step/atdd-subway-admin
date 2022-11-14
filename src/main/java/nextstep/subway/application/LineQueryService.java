package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.LineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LineQueryService {

	private final LineRepository lineRepository;

	public LineQueryService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	public List<LineResponse> findAllLines() {
		return LineResponse.ofList(lineRepository.findAll());
	}

	public LineResponse findLine(Long lineId) {
		Line line = getLine(lineId);
		return LineResponse.of(line);
	}

	private Line getLine(Long lineId) {
		return lineRepository.findById(lineId)
			.orElseThrow(() -> new LineNotFoundException(lineId));
	}
}
