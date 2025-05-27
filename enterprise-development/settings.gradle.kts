rootProject.name = "credit-history"

include(
    "eureka-service",
    "api-gateway-service",
    "user-service",
    "data-service",
    "scoring-service",
    "report-service",
    "notification-service",

    "credit-history-commons",
    "credit-history-commons-kafka",
    "credit-history-commons-grpc",

    "user-service-shared",
    "user-service-grpc",

    "data-service-grpc"
)