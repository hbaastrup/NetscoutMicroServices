package com.netscout.micro.cdr;

import java.util.List;

import hba.tuples.Pair;
import io.javalin.Javalin;

public class WebCdr {
	Javalin app;
	CdrDatabase database;

	public WebCdr(int port, CdrDatabase database) {
		this.database = database;
		
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.start(port);
		
		app.get("/micro/cdr/get/:begin/:end", ctx -> {
			List<CDR> cdrs = database.get(ctx.pathParam("begin"), ctx.pathParam("end"));
			ctx.json(cdrs);
		});
		
		app.get("/micro/cdr/get/time", ctx -> {
			Pair<Integer, Integer> minMax = database.getMinMaxTime();
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
