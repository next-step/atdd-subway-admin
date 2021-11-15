package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

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

	public List<LineResponse> findAll() {
		List<Line> lineList = lineRepository.findAll();
		return lineList
			.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse findById(Long id) {
		Line line = lineRepository.findById(id)
			.orElseThrow(IllegalArgumentException::new);
		return LineResponse.of(line);
	}

	public LineResponse updateLine(Long id, LineRequest lineRequest) {
		Line line = lineRepository.findById(id)
			.orElseThrow(IllegalArgumentException::new);

		line.update(lineRequest.toLine());
		lineRepository.save(line);

		return LineResponse.of(line);
	}

	public void deleteLine(Long id) {
		Line line = lineRepository.findById(id)
			.orElseThrow(IllegalArgumentException::new);

		lineRepository.delete(line);
	}
}
