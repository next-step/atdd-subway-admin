package nextstep.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;

@Service
@Transactional(readOnly = true)
public class LineService {
	private final LineRepository lineRepository;

	public LineService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest) {
		System.out.println("[lineRquest]");
		System.out.println(lineRequest.getColor());
		Line line = lineRepository.save(lineRequest.toLine());
		System.out.println("[save line]");
		System.out.println(line.getColor());
		System.out.println("[reponse line]");
		System.out.println(LineResponse.of(line).getColor());
		return LineResponse.of(line);
	}
}
