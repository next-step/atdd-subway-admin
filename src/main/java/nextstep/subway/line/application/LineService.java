package nextstep.subway.line.application;

import nextstep.subway.common.Distance;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.StationMatch;
import nextstep.subway.section.domain.SectionAddType;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionAddModel;
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
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(NotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(NotFoundException::new);

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
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(NotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(NotFoundException::new);
        Section upSection = sectionRepository.findByUpStationOrDownStationAndLine_Id(upStation, upStation, lineId);
        Section downSection = sectionRepository.findByUpStationOrDownStationAndLine_Id(downStation, downStation, lineId);
        Line line = lineRepository.findById(lineId).orElseThrow(NotFoundException::new);

        Distance distance = new Distance(request.getDistance());
        StationMatch upStationMatch = StationMatch.match(upSection, upStation);
        StationMatch downStationMatch = StationMatch.match(downSection, downStation);

        SectionAddType sectionAddType = SectionAddType.getSectionAddType(upStationMatch, downStationMatch);
        Section addSection = sectionAddType.addSection(new SectionAddModel(upStation, downStation, upSection, downSection, line, distance));
        lineRepository.save(line);

        return SectionResponse.of(addSection);
    }
}
