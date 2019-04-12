package playground.micro.web.cdr.database;

import java.util.List;

import hba.tuples.Pair;
import io.javalin.BadRequestResponse;
import io.javalin.Javalin;

import playground.micro.models.CDR;
import playground.micro.models.MonitorMetric;

public class WebCdr {
	static final String WEB_TIME = "CDR_WEB_TIME";
	static final int WEB_TIME_DURATION = 60000;
	Javalin app;
	String name;

	public WebCdr(int port, String name) {
		this.name = name;
		
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.enableStaticFiles(".");
		app.start(port);
		
		app.get("/micro/cdr/get/:begin/:end", ctx -> {
			List<CDR> cdrs = CdrDatabase.INSTANCE.get(ctx.pathParam("begin"), ctx.pathParam("end"));
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");
			ctx.json(cdrs);
		});
		
		app.get("/micro/cdr/get/time", ctx -> {
			Pair<Integer, Integer> minMax = CdrDatabase.INSTANCE.getMinMaxTime();
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");
			ctx.json("{minTime:"+minMax.getValue0()+", maxTime:"+minMax.getValue1()+"}");
		});
		
		app.get("/micro/get/name", ctx -> {
			ctx.result(name);
		});
		
		app.get("/micro/metic", ctx -> {
			MonitorMetric metric = new MonitorMetric().setName(name);
			ctx.json(metric);
		});

		
		// status=OK: curl -X PUT -F "calling=123456" -F "called=33455" -F "endtime=1554885540413" -F "duration=10000" localhost:10082/micro/cdr/put
		// status=ERROR: curl -X PUT -F "calling=123456" -F "called=33455" -F "endtime=1554885540413" localhost:10082/micro/cdr/put
		app.put("/micro/cdr/put", ctx -> {
			String calling = ctx.formParam("calling");
			if (calling==null) calling = ctx.queryParam("calling");
			String called = ctx.formParam("called");
			if (called==null) called = ctx.queryParam("called");
			String endtime = ctx.formParam("endtime");
			if (endtime==null) endtime = ctx.queryParam("endtime");
			String duration = ctx.formParam("duration");
			if (duration==null) duration = ctx.queryParam("duration");
			CDR cdr = createCDR(calling, called, endtime, duration);
			CdrDatabase.INSTANCE.add(cdr);
		});
		
		app.get("/micro/fail", ctx -> ctx.status(401).json("'err':'Unauthorized'"));
		app.get("/micro/exception", ctx -> {throw new BadRequestResponse("ERROR: Provoked by GET");});
	}
	
	private CDR createCDR(String callingStr, String calledStr, String endtimeStr, String dureationStr) {
		long calling = Long.parseLong(callingStr);
		long called = Long.parseLong(calledStr);
		long endTime = Long.parseLong(endtimeStr);
		int duration = 0;
		if (dureationStr!=null)
			duration = Integer.parseInt(dureationStr);
		CDR cdr;
		if (duration==0) cdr = new CDR(calling, called, endTime);
		else cdr = new CDR(calling, called, endTime-duration, endTime);
		return cdr;
	}
	
	public void close() {}
	
	
	public static void main(String[] args) {
		int port = 10082;
		String name = WebCdr.class.getName();
		
		for (int i=0; i<args.length; i++) {
			if ("-p".equals(args[i])) {
				i++;
				if (i<args.length)
					port = Integer.parseInt(args[i]);
			}
			else if ("-n".equals(args[i])) {
				i++;
				if (i<args.length)
					name = args[i];
			}
		}

		WebCdr web = new WebCdr(port, name);
		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("STOPPING!!!");
				web.close();
				System.out.println("STOPPED!!!");
			}
		});
	}
}
