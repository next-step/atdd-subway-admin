package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository,
        SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId())
            .orElseThrow(() -> new NotFoundException("역정보가 없습니다."));

        Station downStation = stationRepository.findById(request.getDownStationId())
            .orElseThrow(() -> new NotFoundException("역정보가 없습니다."));

        Section section1 = sectionRepository.save(new Section(request.getDistance(), 1, upStation));
        Section section2 = sectionRepository
            .save(new Section(request.getDistance(), 1, downStation));

        Line persistLine = lineRepository.save(request.toLine());
        persistLine.addSections(Arrays.asList(section1, section2));

        return LineResponse.of(persistLine);
    }

    public LineResponse findLine(Long lineId) {
        Line line = findById(lineId);
        return LineResponse.of(line);
    }

    public List<LineResponse> findLines() {
        List<Line> lineList = lineRepository.findAll();
        return lineList
            .stream()
            .map(line -> LineResponse.of(line))
            .collect(Collectors.toList());
    }

    public Long updateLine(Long lineId, LineRequest lineRequest) {
        Line line = findById(lineId);

        line.update(lineRequest.toLine());
        Line save = lineRepository.save(line);
        return save.getId();
    }

    public void deleteLine(Long lineId) {
        Line line = findById(lineId);

        lineRepository.delete(line);
    }

    private Line findById(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(() -> new NotFoundException());
    }

}
