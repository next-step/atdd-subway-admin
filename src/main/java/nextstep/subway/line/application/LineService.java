package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

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

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		return LineResponse.ofList(lineRepository.findAll());
	}

	@Transactional(readOnly = true)
	public LineResponse findLineById(Long id) {
		return lineRepository.findById(id).map(LineResponse::of)
			.orElseThrow(EntityNotFoundException::new);
	}

	public void deleteLineById(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public LineResponse updateLine(Long id, LineRequest request) {
		Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
		line.update(line);
		return LineResponse.of(line);
	}

}
