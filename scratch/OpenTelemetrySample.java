import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.extension.annotations.WithSpan;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

public class OpenTelemetrySample {

    public static void main(String[] args) {
        // Create an instance of the OTLP gRPC Span Exporter
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://127.0.0.1/:4318") // Replace with your collector endpoint
                .build();

        // Create a BatchSpanProcessor and set the exporter to the OTLP exporter
        SpanProcessor spanProcessor = SimpleSpanProcessor.create(spanExporter);

        // Build the tracer provider using the span processor
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .build();

        // Set up OpenTelemetry SDK with the exporter and a simple processor
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).buildAndRegisterGlobal();

        // Get the tracer
        Tracer tracer = openTelemetry.getTracer("sample-tracer");

        // Generate some sample traces
        for (int i = 0; i < 10; i++) {
            Span span = tracer.spanBuilder("sample-span-" + i).startSpan();
            try (Scope scope = span.makeCurrent()) {
                span.setAttribute("sample-attribute", "value-" + i);
                span.addEvent("sample-event-" + i);
                // Simulate some work
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                span.end();
            }
        }

        // Ensure that spans are exported before shutting down
        spanExporter.shutdown();
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

}
