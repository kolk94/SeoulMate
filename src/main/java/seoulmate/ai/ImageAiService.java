package seoulmate.ai;

import org.springframework.stereotype.Service;
import seoulmate.ai.model.ImageCategory;

@Service
public class ImageAiService {

    public AiResult classify(String imagePath) {
        ImageCategory category = ImageCategory.OTHER;
        String label = "UNKNOWN";
        double score = 0.0;

        return new AiResult(label, score, category);
    }

    public record AiResult(
            String label,
            double score,
            ImageCategory category
    ) {
    }
}
