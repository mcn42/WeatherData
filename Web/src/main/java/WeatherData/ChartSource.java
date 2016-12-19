package WeatherData;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

import java.awt.BasicStroke;
import java.awt.Color;

import java.io.IOException;
import java.io.OutputStream;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@WebServlet(name = "ChartSource", urlPatterns = { "/charts" })
public class ChartSource extends HttpServlet {
    private static final String CONTENT_TYPE = "image/png; charset=UTF-8";
    private static final long DATA_REFRESH_PERIOD = 5 * 60 * 1000L;
    private static final double TEMP_PADDING = 5.0;
    private static final double HUMIDITY_PADDING = 10.0;
    private static final double PRESSURE_PADDING = 20.0;
    private static final long TSTAMP_PADDING = 60 * 60 * 1000;

    private MongoClient mongoClient = null;
    private MongoDatabase mongoDb = null;
    private MongoCollection mongoColl = null;

    private String dbHost = "localhost";
    private Integer dbPort = 27017;
    private String databasename = "weather";
    private String collectionName = "observations";

    final XYSeries seriesT = new XYSeries("Temperature");
    final XYSeries seriesH = new XYSeries("Humidity");
    final XYSeries seriesP = new XYSeries("Pressure");

    private Logger logger = Logger.getLogger("WeatherData");

    private Timer t = new Timer();
    private RefreshTask task = null;

    private double minTemp;
    private double maxTemp;
    private double minHumid;
    private double maxHumid;
    private double minPress;
    private double maxPress;
    private long minTstamp;
    private long maxTstamp;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Locale.setDefault(new Locale("en", "US"));
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
        logger.info("Setting Time Zone: " + TimeZone.getDefault().getDisplayName());
        this.start();
    }

    @PreDestroy
    public void preDestroy() {
        this.stop();
    }

    public void start() {
        try {
            mongoClient = new MongoClient(this.dbHost);
            this.mongoDb = this.mongoClient.getDatabase(this.databasename);
            this.mongoColl = this.mongoDb.getCollection(this.collectionName);
            this.task = new RefreshTask();
            this.t.schedule(task, 0L, DATA_REFRESH_PERIOD);
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("MongoDB init error"), e);
            return;
        }
    }


    private void getData(long lastMillis) {
        BasicDBObject query = new BasicDBObject("i", 71);
        long first = System.currentTimeMillis() - lastMillis;
        this.seriesT.clear();
        this.seriesH.clear();
        this.seriesP.clear();

        this.calculateLimits(lastMillis);

        Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(final Document document) {
                ArrayList list = document.get("timestamp", ArrayList.class);
                Long timestamp = (Long) list.get(0);

                list = document.get("tempF", ArrayList.class);
                Double tempF = (Double) list.get(0);

                list = document.get("pressure", ArrayList.class);
                Double pressure = (Double) list.get(0);

                list = document.get("humidity", ArrayList.class);
                Double humidity = (Double) list.get(0);

                seriesT.add(timestamp, tempF);
                seriesH.add(timestamp, humidity);
                seriesP.add(timestamp, pressure);
            }
        };

        mongoColl.find(gt("timestamp", first)).forEach(printBlock);
        System.out.println("Size: " + seriesT.getItems().size());
    }

    public void stop() {
        logger.info(String.format("Stopping WeatherData app '%s'"));
        if (this.mongoClient != null)
            this.mongoClient.close();
        if (this.task != null)
            this.task.cancel();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String chartType = "T";
        try {
            chartType = request.getParameter("type");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (chartType == null)
            chartType = "T";
        response.setContentType(CONTENT_TYPE);
        OutputStream out = response.getOutputStream(); /* Get the output stream from the response object */
        try {
            final XYSeriesCollection dataset = new XYSeriesCollection();
            String yaxis = "Deg F";
            String label = "Temperature";
            Color lineClr = Color.RED;
            double min = 50.0D;
            double max = 100.0D;
            if (chartType.equals("T")) {
                dataset.addSeries(seriesT);
                label = "Temperature";
                yaxis = "Deg. F";
                lineClr = Color.RED;
                min = this.minTemp;
                max = this.maxTemp;
            } else if (chartType.equals("P")) {
                dataset.addSeries(seriesP);
                label = "Pressure";
                yaxis = "millibars";
                lineClr = Color.GREEN;
                min = this.minPress;
                max = this.maxPress;
            } else if (chartType.equals("H")) {
                dataset.addSeries(seriesH);
                label = "Humidity";
                yaxis = "Percent";
                lineClr = Color.BLUE;
                min = this.minHumid;
                max = this.maxHumid;
            }
            // create the chart...
            final JFreeChart chart = ChartFactory.createXYLineChart(String.format("%s - Last 24 HRS", label), // chart title
                                                                    "Time", // x axis label
                                                                    yaxis, // y axis label
                                                                    dataset, // data
                                                                    PlotOrientation.VERTICAL, true, // include legend
                                                                    true, // tooltips
                                                                    false // urls
                                                                    );

            // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
            chart.setBackgroundPaint(Color.white);
            
            // get a reference to the plot for further customisation...
            final XYPlot plot = chart.getXYPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);

            final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesLinesVisible(0, true);
            renderer.setSeriesShapesVisible(0, false);
            plot.setRenderer(renderer);

            // change the auto tick unit selection to integer units only...
            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());

            rangeAxis.setAutoRange(false);
            rangeAxis.setRange(new Range(min, max));

            final NumberAxis dAxis = (NumberAxis) plot.getDomainAxis();
            dAxis.setAutoRange(false);
            dAxis.setRange(new Range(this.minTstamp,this.maxTstamp));
            dAxis.setNumberFormatOverride(new TimeNumberFormat());

            renderer.setSeriesStroke(0,
                                     new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
                                                     new float[] { 10.0f, 6.0f }, 0.0f));
            renderer.setSeriesPaint(0, lineClr);


            response.setContentType(CONTENT_TYPE); /* Set the HTTP Response Type */
            ChartUtilities.writeChartAsPNG(out, chart, 1000, 400); /* Write the data to the output stream */
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close(); /* Close the output stream */
        }

    }

    private void calculateLimits(long lastMillis) {
        long first = System.currentTimeMillis() - lastMillis;
        this.minTstamp = first - TSTAMP_PADDING;
        this.maxTstamp = System.currentTimeMillis() + TSTAMP_PADDING;
        
        Document d = (Document) this.mongoColl
                                    .find(gt("timestamp", first))
                                    .sort(ascending("tempF"))
                                    .first();
        this.minTemp = (Double)d.get("tempF", ArrayList.class).get(0);
        this.minTemp = Math.round(this.minTemp) - TEMP_PADDING;

        d = (Document) this.mongoColl
                           .find(gt("timestamp", first))
                           .sort(descending("tempF"))
                           .first();
        this.maxTemp = (Double)d.get("tempF", ArrayList.class).get(0);
        this.maxTemp = Math.round(this.maxTemp) + TEMP_PADDING;

        d = (Document) this.mongoColl
                           .find(gt("timestamp", first))
                           .sort(descending("humidity"))
                           .first();
        this.maxHumid = (Double)d.get("humidity", ArrayList.class).get(0);
        this.maxHumid = Math.round(this.maxHumid) + HUMIDITY_PADDING;

        d = (Document) this.mongoColl
                           .find(gt("timestamp", first))
                           .sort(ascending("humidity"))
                           .first();
        this.minHumid = (Double)d.get("humidity", ArrayList.class).get(0);
        this.minHumid = Math.round(this.minHumid) - HUMIDITY_PADDING;

        d = (Document) this.mongoColl
                           .find(gt("timestamp", first))
                           .sort(descending("pressure"))
                           .first();
        this.maxPress = (Double)d.get("pressure", ArrayList.class).get(0);
        this.maxPress = Math.round(this.maxPress) + PRESSURE_PADDING;
        
        d = (Document) this.mongoColl
                           .find(gt("timestamp", first))
                           .sort(ascending("pressure"))
                           .first();
        this.minPress = (Double)d.get("pressure", ArrayList.class).get(0);
        this.minPress = Math.round(this.minPress) - PRESSURE_PADDING;

        StringBuilder sb = new StringBuilder().append(String.format("Min. Temp = %s %n", this.minTemp))
                                              .append(String.format("Max. Temp = %s %n", this.maxTemp))
                                              .append(String.format("Min. Humidity = %s %n", this.minHumid))
                                              .append(String.format("Max. Humidity = %s %n", this.maxHumid))
                                              .append(String.format("Min. Pressure = %s %n", this.minPress))
                                              .append(String.format("Max. Pressure = %s %n", this.maxPress))
                                              .append(String.format("Min. Time = %s: %s %n", this.minTstamp, new Date(this.minTstamp)))
                                              .append(String.format("Max. Time = %s: %s %n", this.maxTstamp, new Date(this.minTstamp)));
        logger.info(sb.toString());
    }

    private class RefreshTask extends TimerTask {

        @Override
        public void run() {
            logger.info(String.format("WeatherData retrieving data"));

            try {
                getData(24 * 60 * 60 * 1000L);
            } catch (Exception e) {
                logger.log(Level.SEVERE, String.format("MongoDB find error"), e);
            }
        }
    }
    
    private class TimeNumberFormat extends NumberFormat {
        private DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);

        @Override
        public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
            Long l = new Double(number).longValue();
            return this.format(l, toAppendTo, pos);
        }

        @Override
        public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
            Date d = new Date(number);
            return toAppendTo.append(this.df.format(d));
        }

        @Override
        public Number parse(String source, ParsePosition parsePosition) {
            long time = -1;
            try {
                Date d = df.parse(source);
                time = d.getTime();
            } catch (ParseException e) {
                Logger.getGlobal().log(Level.SEVERE,"Date parse error in TimeNumberFormat",e);
            }
            return time;
        }
    }
}
