package seoulmate.ai.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import seoulmate.ai.ImageAiService;
import seoulmate.image.ImageStorageService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai-test")
public class AiTestController {

    private final ImageStorageService imageStorageService;
    private final ImageAiService imageAiService;

    @PostMapping("/predict")
    public AiTestResponse predict(@RequestParam("image") MultipartFile image) throws IOException {
        String imageUrl = imageStorageService.store(image);
        ImageAiService.AiResult result = imageAiService.classify(imageUrl);
        return new AiTestResponse(
                imageUrl,
                result.label(),
                result.score(),
                result.category().name()
        );
    }

    public record AiTestResponse(
            String imageUrl,
            String label,
            double score,
            String category
    ) {
    }
}
