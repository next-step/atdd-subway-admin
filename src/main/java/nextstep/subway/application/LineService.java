package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineCreateRequest lineCreateRequest) {
        Station upStation = stationService.findStationById(lineCreateRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineCreateRequest.getDownStationId());

        Line line = lineCreateRequest.toLine();
        line.addSection(new Section(line, upStation, downStation, lineCreateRequest.getDistance()));

        Line persistLine = lineRepository.save(line);

        return LineResponse.of(persistLine);
    }

    public List<LineResponse> getAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public LineResponse getLineById(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() ->
            new IllegalArgumentException("해당 id 에 존재하는 Line 이 없습니다.")
        );
        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse updateLine(Long lineId, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(() ->
            new IllegalArgumentException("해당 id 에 존재하는 Line 이 없습니다.")
        );

        line.update(lineUpdateRequest.toLine());

        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLine(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() ->
            new IllegalArgumentException("해당 id 에 존재하는 Line 이 없습니다.")
        );

        lineRepository.delete(line);
    }


}
