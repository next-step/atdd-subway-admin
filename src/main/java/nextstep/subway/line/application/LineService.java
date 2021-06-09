package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineAndStationResponse;
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

    public List<LineAndStationResponse> findAllLines() {
        List<Line> stations = lineRepository.findAll();
        return stations.stream()
            .map(LineAndStationResponse::of)
            .collect(Collectors.toList());
    }
	public LineAndStationResponse findByLine(Long id) {
		return LineAndStationResponse.of(lineRepository.findById(id).orElseThrow(()
			-> new IllegalArgumentException("조회 할 수 없습니다.")));
	}

	public void updateLineById(Long id, LineRequest lineRequest) {
		Line line = lineRepository.findById(id).orElseThrow(()
			-> new IllegalArgumentException("조회 할 수 없습니다."));
		line.update(lineRequest.toLine());
	}
}
