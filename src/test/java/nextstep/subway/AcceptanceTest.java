package nextstep.subway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    protected static final String DELIMITER = "/";

    @LocalServerPort
    protected int port;

    @Autowired
    protected DatabaseCleaner databaseCleaner;
}
