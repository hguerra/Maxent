package ptolemy.plot.servlet;

import com.microstar.xml.XmlException;
import ptolemy.plot.Plot;
import ptolemy.plot.plotml.PlotMLParser;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Takes the parameters file, w, h and returs and jpeg image with the
 * ptplot graph generated from the PlotML file.
 * <p>
 * the file mustbe located in the same directory or a subdirectory of the
 * servlet.
 * <p>
 * Use in your html as:
 * &lt;img src="/servlet/ptolemy/plot/servlet/PlotServlet?file=Bin.xml&w=500&h=500"/&gt;
 * &lt;img src="/servlet/ptolemy/plot/servlet/PlotServlet?file=HighLowSmall.xml&w=500&h=500"/&gt;
 * <p>
 * No waranties given in any respect! Have fun.
 *
 * @author A. Gobbi
 * @version 1.0
 */
public class PlotServlet extends HttpServlet {
    /**
     * Write the jpeg image of the Component cnt to the OutPutStream.
     * <p>
     * If the component is a penal with subpanels you might need to call
     * {@link JPanel#addNotify} and {@link JPanel#doLayout}.
     * If the server is running on Unix you will need to use java 1.4 and specify
     * -Djava.awt.headless=true on the command Line or for tomcat:
     * edit catalina.sh in the $CATALINA_HOME/bin directory and add a line
     * CATALINA_OPTS='-Djava.awt.headless=true'
     */
    static public void writeAsJPEG(OutputStream out, Component cnt)
            throws IOException {
        Dimension s = cnt.getSize();
        BufferedImage bufferedImage = new BufferedImage((int) s.getWidth(),
                (int) s.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.white);

        //graphics.fillRect(0, 0, (int)s.getWidth(), (int)s.getHeight());
        cnt.print(graphics);

        ImageIO.write(bufferedImage, "png", out);

        graphics.dispose();
        bufferedImage.flush();
    }

    /**
     * Parse PlotML file into plot.
     */
    private static void read(Plot plot, InputStream in)
            throws IOException {
        PlotMLParser parser;
        parser = new PlotMLParser(plot);

        try {
            parser.parse(null, in);
        } catch (Exception ex) {
            String msg;

            if (ex instanceof XmlException) {
                XmlException xmlex = (XmlException) ex;
                msg = "PlotMLFrame: failed to parse PlotML data:\n" + "line: "
                        + xmlex.getLine() + ", column: " + xmlex.getColumn()
                        + "\nIn entity: " + xmlex.getSystemId() + "\n";
            } else {
                msg = "PlotMLFrame: failed to parse PlotML data:\n";
            }

            throw new IOException(msg);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fName = request.getParameter("file");

        if (fName == null) {
            throw new ServletException("No filename given!");
        }

        if (fName.indexOf("src/test") >= 0) {
            throw new ServletException("Security problem with filename: "
                    + fName);
        }

        InputStream fileStream = this.getClass().getClassLoader()
                .getResourceAsStream("ptolemy/plot/servlet/"
                        + fName);

        Plot ptPlot;

        try {
            if (fileStream == null) {
                throw new ServletException("Could not find: " + fName);
            }

            ptPlot = new Plot();

            int h = 200;
            int w = 300;
            String dummy = request.getParameter("h");

            if (dummy != null) {
                h = Integer.parseInt(dummy);
            }

            dummy = request.getParameter("w");

            if (dummy != null) {
                w = Integer.parseInt(dummy);
            }

            ptPlot.setSize(w, h);

            read(ptPlot, fileStream);
        } finally {
            fileStream.close();
        }

        response.setContentType("image/jpeg");

        OutputStream out = response.getOutputStream();
        writeAsJPEG(out, ptPlot);
        out.close();
    }
}
