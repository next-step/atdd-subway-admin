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
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;
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

    public LineResponse getLineById(Long id) {
        Line line = this.findLineById(id);

        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = this.findLineById(id);
        line.update(lineUpdateRequest.toLine());

        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = this.findLineById(id);

        lineRepository.delete(line);
    }


    public LineResponse updateSection(Long id, SectionRequest sectionRequest) {
        Line line = this.findLineById(id);

        Section section = getSection(line, sectionRequest);

        line.addSection(section);

        return LineResponse.of(line);
    }

    private Section getSection(Line line, SectionRequest sectionRequest) {
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
        return new Section(line, downStation, upStation, sectionRequest.getDistance());
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new SubwayException(ErrorCode.LINE_NULL_POINTER_ERROR));
    }
}
