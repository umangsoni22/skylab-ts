package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.extension.annotations.WithSpan;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;



public class MyHandler implements RequestHandler<MyHandler.IntegerRecord, Integer> {

    private static final Tracer tracer;

    static {
        OtlpGrpcSpanExporter exporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://ip-10-0-176-41.ec2.internal:4317") // replace with your EC2 instance IP and OTLP port
                .build();

        // Create a BatchSpanProcessor and set the exporter to the OTLP exporter
        BatchSpanProcessor spanProcessor = BatchSpanProcessor.builder(exporter).build();

        // Build the tracer provider using the span processor
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .build();

        // Initialize the global OpenTelemetry instance with the tracer provider
        OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).buildAndRegisterGlobal();
        tracer = GlobalOpenTelemetry.getTracer("com.example.tracer");
    }

    @WithSpan
    private void execute1() throws InterruptedException {
        System.out.println("Executing execute1");
        Thread.sleep(1000);
    }

    @WithSpan
    private void execute2() throws InterruptedException {
        System.out.println("Executing execute2");
        Thread.sleep(2000);
    }

    @WithSpan
    private void execute3() throws InterruptedException {
        System.out.println("Executing execute3");
        Thread.sleep(5000);
    }

    @Override
    public Integer handleRequest(IntegerRecord integerRecord, Context context) {
        Span span = tracer.spanBuilder("handleRequest").startSpan();
        try (Scope scope = span.makeCurrent()) {

            execute1();
            execute2();
            execute3();

            span.setStatus(StatusCode.OK);

            // your handler code here
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            span.end();
        }

        return null;
    }

    record IntegerRecord(int x, int y, String message) {

    }

}
