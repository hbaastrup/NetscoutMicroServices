package playground.micro.web.cdr.producer;

import java.util.List;

import io.javalin.Javalin;
import playground.micro.models.CommandMetricsHolder;
import playground.micro.models.MonitorMetric;

public class CdrProducerWeb {
	Javalin app;

	public CdrProducerWeb(int port) {
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.enableStaticFiles(".");
		app.start(port);

		app.get("/micro/producer/cdr/metric", ctx -> {
			MonitorMetric metric = new MonitorMetric();
			List<CommandMetricsHolder> mitricList = CommandMetricsHolder.instanceHystrixCommandMetricsList();
			metric.setCommandMetrics(mitricList);
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");
			ctx.json(metric);
		});
	}
	
	public void close() {}
}
