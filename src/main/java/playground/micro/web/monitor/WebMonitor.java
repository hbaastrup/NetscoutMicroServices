package playground.micro.web.monitor;

import java.util.List;

import hba.gc.GcEvent;
import hba.gc.GcEventListener;
import hba.gc.GcEventLogger;
import io.javalin.Javalin;
import playground.micro.models.EnpointStatus;
import playground.micro.monitor.Monitor;

public class WebMonitor implements GcEventListener {
	Javalin app;
	String name;
	Monitor monitor;
	GcEventLogger gcEventLogger;
	
	public WebMonitor(int port, String name, Monitor monitor) {
		gcEventLogger = new GcEventLogger();
		gcEventLogger.start(this);
		
		this.name = name;
		this.monitor = monitor;
		
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.enableStaticFiles(".");
		app.start(port);

		app.get("/micro/monitor/get", ctx -> {
			List<EnpointStatus> status = monitor.getAllStatus();
			ctx.json(status);
		});
		
		app.get("/micro/get/name", ctx -> {
			ctx.result(name);
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
