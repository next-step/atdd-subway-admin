package nextstep.subway.line.application;

import nextstep.subway.common.Distance;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {
    private static final String LINE_NOT_FOUND_MESSAGE = "원하시는 지하천 노선을 찾지 못했습니다.";

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = station(request.getUpStationId());
        Station downStation = station(request.getDownStationId());

        return LineResponse.of(lineRepository.save(request.toLine(upStation, downStation)));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> selectAllLines() {
        return LineResponse.ofList(lineRepository.findAll());
    }

    @Transactional(readOnly = true)
    public LineResponse selectLine(Long id) {
        return LineResponse.of(lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND_MESSAGE)));
    }

    public LineResponse modifyLine(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND_MESSAGE));
        line.update(request.toLine());
        return LineResponse.of(lineRepository.save(line));
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public SectionResponse addSection(Long lineId, SectionRequest request) {
        Station upStation = station(request.getUpStationId());
        Station downStation = station(request.getDownStationId());
        Line line = line(lineId);
        Section addSection = line.toSection(upStation, downStation, new Distance(request.getDistance()));
        lineRepository.save(line);

        return SectionResponse.of(addSection);
    }

    public void deleteSection(long lineId, long stationId) {
        Station deleteStation = station(stationId);
        Line line = line(lineId);
        line.deleteSection(deleteStation);
        lineRepository.save(line);
    }

    private Station station(Long upStationId) {
        return stationRepository.findById(upStationId).orElseThrow(NotFoundException::new);
    }

    private Line line(long lineId) {
        return lineRepository.findById(lineId).orElseThrow(NotFoundException::new);
    }


}
