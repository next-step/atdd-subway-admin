package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
	private final LineRepository lineRepository;

	public LineService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		Line persistLine = lineRepository.save(request.toLine());
		return LineResponse.of(persistLine);
	}

	public List<LineResponse> getLines() {
		return lineRepository.findAll().stream().map(LineResponse::of)
				.collect(Collectors.toList());
	}

	public Line findLineById(Long lineId) {
		return lineRepository.findById(lineId)
				.orElseThrow(() -> new LineNotFoundException(lineId));
	}

	public void modifyLine(Long lineId, LineRequest request) {
		Line line = findLineById(lineId);
		line.update(request.toLine());
	}
}
