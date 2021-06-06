package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.NoSuchLineException;

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

	public List<LineResponse> getLines() {
    	List<Line> lines = lineRepository.findAll();
		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse getLine(Long id) {
		Line line = getLineById(id);
		return LineResponse.of(line);
	}

	public void updateLine(Long id, LineRequest lineRequest) {
		Line line = getLineById(id);
		line.update(lineRequest.toLine());
	}

	private Line getLineById(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(() -> new NoSuchLineException("존재하지 않는 노선입니다."));
	}
}
