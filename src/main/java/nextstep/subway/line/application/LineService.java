package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineService {

	private final LineRepository lineRepository;

	@Transactional
	public LineResponse saveLine(LineRequest request) {
		Line persistLine = lineRepository.save(request.toLine());
		return LineResponse.of(persistLine);
	}

	public List<LineResponse> getLines() {
		List<Line> lines = lineRepository.findAll();
		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse getLineById(Long id) {
		Line line = findById(id);
		return LineResponse.of(line);
	}

	@Transactional
	public LineResponse updateLine(Long id, LineRequest request) {
		Line line = findById(id);
		line.update(request);
		return LineResponse.of(line);
	}

	@Transactional
	public void deleteLine(Long id) {
		lineRepository.delete(findById(id));
	}

	private Line findById(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(() -> new LineNotFoundException("노선 정보를 찾을 수 없습니다."));
	}
}
