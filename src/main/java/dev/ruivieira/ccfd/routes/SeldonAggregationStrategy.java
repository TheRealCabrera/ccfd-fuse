package dev.ruivieira.ccfd.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.ruivieira.ccfd.routes.messages.v1.PredictionRequest;
import dev.ruivieira.ccfd.routes.messages.v0.PredictionResponse;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class SeldonAggregationStrategy implements AggregationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(SeldonAggregationStrategy.class);

    private Boolean USE_SELDON_STANDARD;

    private final ObjectMapper responseMapper = new ObjectMapper();

    public SeldonAggregationStrategy() {
        USE_SELDON_STANDARD = System.getenv("SELDON_STANDARD") != null;
        responseMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
    }

    public Exchange aggregate(Exchange original, Exchange resource) {
        Object originalBody = original.getIn().getBody();
        Object resourceResponse = resource.getIn().getBody(String.class);


        List<Double> features = new ArrayList<>();

        try {
            if (USE_SELDON_STANDARD) {
                PredictionRequest request = PredictionRequest.fromString(originalBody.toString());
                // build KIE server data payload
                features = request.getData().getOutcomes().get(0);
            } else {
                ObjectMapper requestMapper = new ObjectMapper();
                Map<String, String> map = requestMapper.readValue(originalBody.toString(), Map.class);
                String[] featureString = map.get("strData").split(",");
                for (String f : featureString) {
                    features.add(Double.parseDouble(f));
                }
            }

            Boolean fraudulent;

            if (USE_SELDON_STANDARD) {
                dev.ruivieira.ccfd.routes.messages.v1.PredictionResponse response = responseMapper.readValue(resourceResponse.toString(), dev.ruivieira.ccfd.routes.messages.v1.PredictionResponse.class);
                fraudulent = response.getData().getOutcomes().get(0).get(0) >= response.getData().getOutcomes().get(0).get(1);
            } else {
                dev.ruivieira.ccfd.routes.messages.v0.PredictionResponse response = responseMapper.readValue(resourceResponse.toString(), dev.ruivieira.ccfd.routes.messages.v0.PredictionResponse.class);
                List<Double> prediction = response.getData().getOutcomes();
                fraudulent = prediction.get(0) <= 0.5;
            }

            Map<String, Object> mergeResult = new HashMap<>();

            // generate a random custom id
            mergeResult.put("customer_id", new Random().nextInt(10000));
            // add selected features from the Kaggle dataset
            mergeResult.put("v3", features.get(0));
            mergeResult.put("v4", features.get(1));
            mergeResult.put("v10", features.get(2));
            mergeResult.put("v11", features.get(3));
            mergeResult.put("v12", features.get(4));
            mergeResult.put("v14", features.get(5));
            mergeResult.put("v17", features.get(6));
            mergeResult.put("v29", features.get(7));

            logger.info("Merged payload: " + mergeResult.toString());


            if (original.getPattern().isOutCapable()) {
                original.getOut().setBody(mergeResult, Map.class);
                original.getOut().setHeader("fraudulent", fraudulent);
            } else {
                original.getIn().setBody(mergeResult, Map.class);
                original.getIn().setHeader("fraudulent", fraudulent);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return original;
    }
}
