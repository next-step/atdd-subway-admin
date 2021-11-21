package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		return lineRepository.findAll()
			.stream()
			.map(line -> LineResponse.of(line))
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findById(Long id) {
		Optional<Line> optionalLine = lineRepository.findById(id);
		return LineResponse.of(ifPresentGetElseException(optionalLine));
	}

	public LineResponse updateById(Long id, LineRequest lineRequest) {
		Optional<Line> optionalLine = lineRepository.findById(id);
		Line line = ifPresentGetElseException(optionalLine);
		line.update(lineRequest.toLine());
		return LineResponse.of(line);
	}

	private Line ifPresentGetElseException(Optional<Line> optionalLine) {
		if (!optionalLine.isPresent()) {
			throw new IllegalArgumentException("존재하지 않는 노선입니다.");
		}
		return optionalLine.get();
	}
}
