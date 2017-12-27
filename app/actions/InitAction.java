package actions;

import com.fasterxml.jackson.databind.JsonNode;
import models.AnnotationModel;
import org.apache.commons.lang3.RandomStringUtils;
import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import scala.concurrent.impl.Promise;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import org.slf4j.MDC;

public class InitAction extends Action.Simple {

    private static final Logger.ALogger logger = Logger.of(InitAction.class);
    private static final String DATE_TIME_PATTERN = "dd.MM.yy_HH.mm.ss";
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat(DATE_TIME_PATTERN);

    @Override
    public CompletionStage<Result> call(Http.Context ctx) {
        try{
            String requestId = createRequestId();
            ctx.args.put("_req", requestId);
            if(ctx.args.containsKey("_req")){
                requestId = String.valueOf(ctx.args.get("_req"));
            }
            String prefix=" req="+requestId;
            if(ctx.response() != null){
                ctx.response().setHeader("request_id", requestId);
            }
            MDC.put("request_id", prefix);
            //  ApplicationLogger.setLoggerRequestId(null);
            logger.debug("Got request : " + ctx.request().uri() + " " + ctx.request().method() + " " + ctx.request().path() + " " + requestId);
        } catch(Exception e){
            logger.error("Something went wrong in loaduser action " + e.getMessage(),e);
            return (CompletionStage<Result>) internalServerError("opps something went bas.. we are working on it");
        }

        CompletionStage<Result> completionStage = delegate.call(ctx);


        return completionStage;
    }

    private String createRequestId() {
        StringBuilder requestIdBuilder = new StringBuilder("GE");
        requestIdBuilder.append(RandomStringUtils.randomAlphanumeric(5));

        requestIdBuilder.append("_");
        try {
            InetAddress hostAddress = InetAddress.getLocalHost();
            requestIdBuilder.append(hostAddress.getHostAddress());
        } catch (UnknownHostException e) {
            requestIdBuilder.append("NO.HOST");
            logger.warn("Exception while getting ip address for the GE host ", e);
        }

        String currentDateTime = DATE_TIME_FORMAT.format(new Date());
        requestIdBuilder.append("_");
        requestIdBuilder.append(currentDateTime);

        return requestIdBuilder.toString();
    }


}
