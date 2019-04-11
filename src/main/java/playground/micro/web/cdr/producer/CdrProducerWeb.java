package playground.micro.web.cdr.producer;

import java.util.List;

import io.javalin.Javalin;
import playground.micro.models.CommandMetricsHolder;
import playground.micro.models.MonitorMetric;
import playground.micro.producers.CdrProducer;

public class CdrProducerWeb {
	Javalin app;
	CdrProducer cdrProducer;

	public CdrProducerWeb(int port, CdrProducer cdrProducer) {
		this.cdrProducer = cdrProducer;
		
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.enableStaticFiles(".");
		app.start(port);

		app.get("/micro/metic", ctx -> {
			MonitorMetric metric = new MonitorMetric();
			metric.putParameter("lastRun", cdrProducer.getLastRun());
			List<CommandMetricsHolder> mitricList = CommandMetricsHolder.instanceHystrixCommandMetricsList(cdrProducer.getMaxWebWaitRequestTime());
			metric.setCommandMetrics(mitricList);
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");
			ctx.json(metric);
		});
	}
	
	public void close() {}
}
