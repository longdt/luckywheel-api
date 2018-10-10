package com.foxpify.luckywheel.handler;

import com.foxpify.luckywheel.exception.BusinessException;
import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.model.response.ErrorResponse;
import com.foxpify.luckywheel.util.Constant;
import com.foxpify.luckywheel.util.Responses;
import com.github.mauricio.async.db.postgresql.exceptions.GenericDatabaseException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.collection.immutable.Map;

public class ExceptionHandler {
    private static final Logger logger = LogManager.getLogger(ExceptionHandler.class);

    public static void handle(RoutingContext routingContext) {
        Throwable cause = routingContext.failure();
        if (routingContext.statusCode() == 401) {
            cause = new BusinessException(ErrorCode.UNAUTHENTICATED_ERROR, (String) null);
        }
        handle(routingContext, cause);
    }

    public static void handle(RoutingContext routingContext, Throwable cause) {
        if (cause instanceof GenericDatabaseException) {
            Map<Object, String> errorInfo = ((GenericDatabaseException) cause).errorMessage().fields();
            String name = errorInfo.get('n').getOrElse(null);
            if (Constant.SUBSRIBER_UNIQUE_ERROR.equals(name)) {
                cause = new BusinessException(ErrorCode.SUBSCRIBER_EXISTS_ERROR, cause.getMessage(), cause);
            }
        } else if (!(cause instanceof BusinessException)) {
            cause = new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, cause.getMessage(), cause);
        }

        handleBusinessException(routingContext, (BusinessException) cause);
    }

    private static ErrorResponse makeResponse(BusinessException cause) {
        return new ErrorResponse(cause.getErrorCode().code(), cause.getErrorCode().description(), cause.getMessage());
    }

    private static void handleBusinessException(RoutingContext routingContext, BusinessException cause) {
        ErrorResponse errorResponse = makeResponse(cause);
        log(errorResponse, cause);
        Responses.json(routingContext, cause.getErrorCode().httpStatus(), errorResponse);
    }

    private static void log(ErrorResponse errorResponse, BusinessException cause) {
        String msg = Json.encode(errorResponse);
        if (isClientError(cause)) {
            logger.warn(msg, cause);
        } else {
            logger.error(msg, cause);
        }
    }

    private static boolean isClientError(BusinessException cause) {
        return cause.getErrorCode().httpStatus() < 500;
    }

    public static boolean isServerError(BusinessException cause) {
        return cause.getErrorCode().httpStatus() >= 500;
    }
}
