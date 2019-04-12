package playground.micro.web.orchestrator;

import io.javalin.Javalin;
import playground.micro.models.MonitorMetric;

public class WebOrchestrator {
	Javalin app;
	String name;

	public WebOrchestrator(int port, String name) {
		this.name = name;
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.enableStaticFiles(".");
		app.start(port);

		app.get("/micro/metic", ctx -> {
			MonitorMetric metric = new MonitorMetric().setName(name);
			ctx.json(metric);
		});
	}
	
	public void close() {}
}
