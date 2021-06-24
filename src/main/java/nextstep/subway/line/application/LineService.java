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

        return LineResponse.of(lineRepository.save(request.toLine(section)));
    }

    // 기존에 있는 Section에 새로운 Section을 추가한다.
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

    public LineResponse addSections(long lineId, SectionRequest request) {
        // line 을 조회한다.
        // 조회한 line에 구간을 가져와 상 또는 하 중 있는 곳에 연결한다.
        // 상이나 하 중 일치하는 곳이 하나 존재한ㄷ.ㅏ
        // 만약 둘 다 없는 경우 예외처리 한다.
        // 만약 둘 다 존재하는 경우 예외처리 한다.
        Line line = findByIdOrThrow(lineId);


//        Station upStation = stationService.findById(request.getUpStationId());
//        Station downStation = stationService.findById(request.getDownStationId());

        // line에 존재하는 Section을 가져온다.

        List<Section> sections = line.getSections();

        for (int i = 0; i < sections.size(); i++) {
            // 요청된 것 중에 statinos 가 일치하지 않는 것이 있는지 확인한다
            Section section = sections.get(i);
            long downStationId = request.getDownStationId();
            long upStationId = request.getUpStationId();

            if (section.getDownStation().getId() == downStationId && section.getUpStation().getId() == upStationId) {
                throw new IllegalArgumentException();
            }
            // sections가

            if (section.getDownStation().getId() == downStationId) {
                // 상행을 요청한 것으로 변경한다.
                // 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
                if ()
            }

            if (section.getUpStation().getId() == upStationId) {

            }
        }
    }

}
