package playground.micro.cdr;

import java.util.List;

import hba.tuples.Pair;

import io.javalin.Javalin;

import playground.micro.kpi.KpiHolder;
import playground.micro.kpi.TimeSeries;
import playground.micro.kpi.TimeTaken;
import playground.micro.models.CDR;

public class WebCdr {
	static final String WEB_TIME = "CDR_WEB_TIME";
	static final int WEB_TIME_DURATION = 60000;
	Javalin app;
	CdrDatabase database;

	public WebCdr(int port, CdrDatabase database) {
		this.database = database;
		
		KpiHolder.INSTANCE.add(WEB_TIME, new TimeSeries(WEB_TIME_DURATION));
		
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.enableStaticFiles(".");
		app.start(port);
		
		app.get("/micro/cdr/get/:begin/:end", ctx -> {
			long startTime = System.currentTimeMillis();
			List<CDR> cdrs = database.get(ctx.pathParam("begin"), ctx.pathParam("end"));
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");
			KpiHolder.INSTANCE.add(WEB_TIME, new TimeTaken((int)(System.currentTimeMillis()-startTime)));
			ctx.json(cdrs);
		});
		
		app.get("/micro/cdr/get/time", ctx -> {
			long startTime = System.currentTimeMillis();
			Pair<Integer, Integer> minMax = database.getMinMaxTime();
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");
			KpiHolder.INSTANCE.add(WEB_TIME, new TimeTaken((int)(System.currentTimeMillis()-startTime)));
			ctx.json("{minTime:"+minMax.getValue0()+", maxTime:"+minMax.getValue1()+"}");
		});
	}
	
	public void close() {}
	
	
	public static void main(String[] args) {
		int port = 10082;
		String subscriberEndpoint = "http://localhost:10081";
		
		for (int i=0; i<args.length; i++) {
			if ("-p".equals(args[i])) {
				i++;
				if (i<args.length)
					port = Integer.parseInt(args[i]);
			}
			else if ("-s".equals(args[i])) {
				i++;
				if (i<args.length)
					subscriberEndpoint = args[i];
			}
		}

		CdrDatabase database = new CdrDatabase();
		CdrProducer producer = new CdrProducer(subscriberEndpoint, database);
		WebCdr web = new WebCdr(port, database);
		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("STOPPING!!!");
				web.close();
				producer.stopThread();
				System.out.println("STOPPED!!!");
			}
		});
	}
}
