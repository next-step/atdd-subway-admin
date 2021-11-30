package nextstep.subway.line.application;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        checkDuplicateLineName(lineRequest.getName());

        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(() -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION));

        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findOneLine(Long id) {
        Line foundLine = lineRepository.findById(id)
                .orElseThrow(() -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_LINE));
        return LineResponse.of(foundLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line modifyLine = lineRepository.getOne(id);
        Station upStaion = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(() -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION));
        Station downStaion = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION));
        modifyLine.update(lineRequest.toLine(upStaion, downStaion));
        return LineResponse.of(modifyLine);
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        sectionRequest.checkValidRequestValue();
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_LINE));
        Section newSection = createSection(line, sectionRequest);
        line.addSection(newSection);
        return LineResponse.of(lineRepository.save(line));
    }

    private Section createSection(Line line, SectionRequest sectionRequest) {
        Station preStation = findStation(sectionRequest.getUpStationId());
        Station station = findStation(sectionRequest.getDownStationId());
        return new Section(line, preStation, station, sectionRequest.getDistance());
    }

    private Station findStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION));
    }

    private void checkDuplicateLineName(String name) {
        if (lineRepository.existsByName(name)) {
            throw new InputDataErrorException(InputDataErrorCode.THERE_IS_A_DUPLICATE_NAME);
        }
    }
}
