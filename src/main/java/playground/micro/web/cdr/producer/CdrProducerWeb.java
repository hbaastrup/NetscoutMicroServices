package playground.micro.web.cdr.producer;

import java.util.List;

import hba.gc.GcEvent;
import hba.gc.GcEventListener;
import hba.gc.GcEventLogger;
import io.javalin.Javalin;
import playground.micro.models.CommandMetricsHolder;
import playground.micro.models.MonitorMetric;
import playground.micro.producers.CdrProducer;

public class CdrProducerWeb implements GcEventListener {
	Javalin app;
	String name;
	CdrProducer cdrProducer;
	GcEventLogger gcEventLogger;

	public CdrProducerWeb(int port, String name, CdrProducer cdrProducer) {
		gcEventLogger = new GcEventLogger();
		gcEventLogger.start(this);
		
		this.name = name;
		this.cdrProducer = cdrProducer;
		
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.enableStaticFiles(".");
		app.start(port);

		app.get("/micro/metic", ctx -> {
			MonitorMetric metric = new MonitorMetric().setName(name);
			metric.putParameter("lastRun", cdrProducer.getLastRun());
			List<CommandMetricsHolder> mitricList = CommandMetricsHolder.instanceHystrixCommandMetricsList(cdrProducer.getMaxWebWaitRequestTime());
			metric.setCommandMetrics(mitricList);
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");
			ctx.json(metric);
		});
		
		app.get("/micro/gc", ctx -> {
			ctx.json(gcEventLogger.getLogs());
		});
		
		app.get("/micro/get/name", ctx -> {
			ctx.result(name);
		});
		
	}
	
	public void close() {
		gcEventLogger.stop();
	}

	@Override
	public void onComplete(GcEvent event) {}
}
