package playground.micro.web.orchestrator;

import hba.gc.GcEvent;
import hba.gc.GcEventListener;
import hba.gc.GcEventLogger;
import io.javalin.Javalin;
import playground.micro.models.MonitorMetric;

public class WebOrchestrator implements GcEventListener {
	Javalin app;
	String name;
	GcEventLogger gcEventLogger;

	public WebOrchestrator(int port, String name) {
		gcEventLogger = new GcEventLogger();
		gcEventLogger.start(this);
		
		this.name = name;
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.enableStaticFiles(".");
		app.start(port);

		app.get("/micro/metic", ctx -> {
			MonitorMetric metric = new MonitorMetric().setName(name);
			ctx.json(metric);
		});
		
		app.get("/micro/gc", ctx -> {
			ctx.json(gcEventLogger.getLogs());
		});
	}
	
	public void close() {
		gcEventLogger.stop();
	}

	@Override
	public void onComplete(GcEvent event) {}
}
