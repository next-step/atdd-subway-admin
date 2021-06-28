package nextstep.subway.line.application;

import nextstep.subway.exception.NotExistLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse save(LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        Section section = new Section(upStation, downStation, request.getDistance());

        Line savedLine = lineRepository.save(request.toLine(section));

        return LineResponse.of(savedLine);
    }

    public LinesResponse findAll() {
        List<Line> lines = lineRepository.findAll();
        return LinesResponse.of(lines);
    }

    public LineResponse findById(Long id) {
        Line line = findByIdOrThrow(id);

        return LineResponse.of(line);
    }

    public void modify(Long id, LineRequest request) {
        Line line = findByIdOrThrow(id);
        line.update(request.toLine());
    }

    public Line findByIdOrThrow(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotExistLineException(id));
    }

    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSections(long lineId, SectionRequest request) {
        Line line = findByIdOrThrow(lineId);

        Station newUpStation = stationService.findById(request.getUpStationId());
        Station newDownStation = stationService.findById(request.getDownStationId());

        line.validateAndAddSections(request.getDistance(), newUpStation, newDownStation);
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        // 만약 전체 Section의 개수가 1개인 경우는 삭제 불가능하다.
        // 해당 Station을 가지고 있는 Section을 추출한다.
        // 해당 section이 종점인 경우(lastIndexOf인 경우) 이전 Section의 하행이 해당 Section의 상행으로 변경한다.
        // 만약 중간 역인 경우에는 해당 이전 section의 상행과 이후 section의 하행으로 길이를 늘린 section을 만든다.
    }
}
