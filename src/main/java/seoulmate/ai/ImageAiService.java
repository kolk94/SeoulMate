package seoulmate.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ByteArrayResource;
import seoulmate.ai.model.ImageCategory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageAiService {

    @Value("${ai.base-url:http://localhost:8000}")
    private String aiBaseUrl;

    @Value("${ai.internal-token:}")
    private String internalToken;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private final RestTemplate restTemplate = new RestTemplate();

    public AiResult classifyImage(byte[] imageBytes) {
        try {
            ByteArrayResource resource = new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return "upload";
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            if (internalToken != null && !internalToken.isBlank()) {
                headers.set("X-INTERNAL-TOKEN", internalToken);
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            String url = aiBaseUrl + "/predict";

            ResponseEntity<AiResponse> responseEntity =
                    restTemplate.postForEntity(url, requestEntity, AiResponse.class);

            AiResponse aiResp = responseEntity.getBody();
            if (aiResp == null) {
                return new AiResult("NO_RESPONSE", 0.0, ImageCategory.OTHER);
            }

            ImageCategory category;
            try {
                category = ImageCategory.valueOf(aiResp.category.toUpperCase());
            } catch (IllegalArgumentException e) {
                category = ImageCategory.OTHER;
            }

            String label = aiResp.category;
            double score = aiResp.confidence;

            return new AiResult(label, score, category);

        } catch (Exception e) {
            throw new IllegalStateException("AI_SERVER_ERROR: " + e.getMessage(), e);
        }
    }

    public static class AiResponse {
        public String category;
        public double confidence;
    }

    public record AiResult(
            String label,
            double score,
            ImageCategory category
    ) {
    }
}
