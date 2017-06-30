package com.cgz;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.cgz.geomath.Point;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = Logger.getLogger(Handler.class);

    private static ApplicationContext applicationContext;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Handler.class);
        app.setWebEnvironment(false);
        applicationContext = app.run(new String[0]);
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: " + input);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");


        try {
            MapController mapController = applicationContext.getBean(MapController.class);
            Map<String, String> queryParams = (Map<String, String>) input.get("queryStringParameters");

            List<Point> result = new ArrayList<>();
            if (queryParams != null && queryParams.size() == 4) {
                result = mapController.getPoints(queryParams);
            }

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(result)
                    .setHeaders(headers)
                    .build();
        } catch (Exception e) {
            LOG.error("Error: " + e);
            e.printStackTrace();
        }

        return ApiGatewayResponse.builder()
                .setStatusCode(400)
                .setObjectBody("error")
                .setHeaders(headers)
                .build();
    }

    //TODO testy
    //TODO error handling
    //TODO replace spring wth sth lightweight
    //TODO make it groovy class

}


