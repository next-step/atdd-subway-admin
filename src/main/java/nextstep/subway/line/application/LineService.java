package nextstep.subway.line.application;

import java.util.List;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public List<LineResponse> findAll() {
        // TODO : Line 전체 목록 구현 필요
        /*
        [
            {
                "id": 1,
                    "name": "신분당선",
                    "color": "bg-red-600",
                    "stations": [

                   ],
                "createdDate": "2020-11-13T09:11:52.084",
                "modifiedDate": "2020-11-13T09:11:52.084"
            },
            {
                "id": 2,
                    "name": "2호선",
                    "color": "bg-green-600",
                    "stations": [

                    ],
                "createdDate": "2020-11-13T09:11:52.098",
                "modifiedDate": "2020-11-13T09:11:52.098"
            }
        ]
        */
        return null;
    }

    public LineResponse findById(Long lineId) {
        // TODO : Line 전체 목록 구현 필요
        /*
        {
            "id": 1,
                "name": "신분당선",
                "color": "bg-red-600",
                "stations": [

                   ],
            "createdDate": "2020-11-13T09:11:52.084",
            "modifiedDate": "2020-11-13T09:11:52.084"
        }
        */
        return null;
    }

    public LineResponse saveLine(LineRequest request) {
        // TODO : Line 중복된 값 검사 BadRequest
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        // TODO : Line Update
        return null;
    }

    public void deleteLineById(Long id) {
        // TODO : Line 삭제 구현 필요
    }
}
