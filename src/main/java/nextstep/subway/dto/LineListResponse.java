package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;

public class LineListResponse {
    private List<LineItem> lineItems = new ArrayList<>();

    public LineListResponse(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public static class LineItem {
        private Long id;

        public LineItem(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }
}
