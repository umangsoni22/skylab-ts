import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.resources.Resource;

public class Main {

    private static Tracer TRACER;

    public static void main(String[] args) {
        configureOpenTelemetry();

        process();
    }

    public static void process() {
        Span processSpan = TRACER.spanBuilder("process").setSpanKind(SpanKind.INTERNAL).startSpan();
        try (Scope scope = processSpan.makeCurrent()) {
            processSpan.addEvent("Start process");
            execute1();
            execute2();
            execute3();
            processSpan.addEvent("End process");
        } catch (Exception e) {
            processSpan.recordException(e);
            processSpan.setStatus(StatusCode.ERROR, "Exception thrown when processing");
        } finally {
            processSpan.end();
        }
    }

    public static void execute1() throws InterruptedException {
        Span span = TRACER.spanBuilder("execute1").setSpanKind(SpanKind.INTERNAL).startSpan();
        try (Scope scope = span.makeCurrent()) {
            // Your logic here
            Thread.sleep(1500);

        } finally {
            span.end();
        }
    }

    public static void execute2() throws InterruptedException {
        Span span = TRACER.spanBuilder("execute2").setSpanKind(SpanKind.INTERNAL).startSpan();
        try (Scope scope = span.makeCurrent()) {
            // Your logic here
            Thread.sleep(2000);
        } finally {
            span.end();
        }
    }

    public static void execute3() throws InterruptedException {
        Span span = TRACER.spanBuilder("execute3").setSpanKind(SpanKind.INTERNAL).startSpan();
        try (Scope scope = span.makeCurrent()) {
            // Your logic here
            Thread.sleep(1000);
        } finally {
            span.end();
        }
    }

    public static void configureOpenTelemetry() {
        Resource serviceNameResource = Resource.builder().put("service.name", "sample-trace-service").build();
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.getDefault();
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .setResource(Resource.getDefault().merge(serviceNameResource))
                .addSpanProcessor(SimpleSpanProcessor.create(spanExporter))
                .build();

        TRACER = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal().getTracer("sample-trace-service");
    }
}
