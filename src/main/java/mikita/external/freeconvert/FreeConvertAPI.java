package mikita.external.freeconvert;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FreeConvertAPI {

    public static final String BASE_URL = "https://api.freeconvert.com/v1/";

    private final RestTemplate restTemplate;

    public FreeConvertAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



}
