# gRPC Logger for Android [![](https://jitpack.io/v/adityam49/GrpcLogger.svg)](https://jitpack.io/#adityam49/GrpcLogger)

A simple and easy to use gRPC request/response logging library inspired by [Chucker](https://github.com/ChuckerTeam/chucker) for android.

## Features
- Easy to use
- Log gRPC requests and responses
- Enable/disable logging
- Share logs
  
## Getting Started
```gradle
dependencies {
    debugImplementation("com.github.adityam49.GrpcLogger:grpc-logger:1.1")
    releaseImplementation("com.github.adityam49.GrpcLogger:no-op-grpc-logger:1.1")
}
```
The debug dependency enables the logging of the gRPC requests.
The release dependency is an empty placeholder that does not include any logging related code in release builds.

### Usage
Add the gRPC interceptor when creating your gRPC channel
```
    val grpcChannel: ManagedChannel = OkHttpChannelBuilder
        .forAddress(baseURL,123)
        .apply {
            intercept(GrpcLogger(context).getInterceptor())
        }
        .build()
```
