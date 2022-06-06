package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final LineService lineService;
    private final StationService stationService;

    public SectionService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public LineResponse saveSections(Long id, LineRequest lineRequest) {
        Line line = lineService.findLineById(id);
        Section section = createSection(
                lineRequest.getDistance(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId()
        );

        line.addSection(section);
        return LineResponse.of(line);
    }

    private Section createSection(int distance, Long upStationId, Long downStationId) {
        return Section.of(
                distance,
                stationService.findStation(upStationId),
                stationService.findStation(downStationId)
        );
    }

}
