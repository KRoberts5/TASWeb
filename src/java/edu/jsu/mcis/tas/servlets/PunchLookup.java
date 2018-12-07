package edu.jsu.mcis.tas.servlets;

import edu.jsu.mcis.tas.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PunchLookup", urlPatterns = {"/PunchLookup"})
public class PunchLookup extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        try {
            
            TASDatabase db = new TASDatabase();
            Punch p = new Punch();
            
            String badgeid = request.getParameter("badgeid").toUpperCase().trim();
            String punchdate = request.getParameter("punchdate").trim();
            
            int month = Integer.parseInt(punchdate.substring(5, 7));
            int day = Integer.parseInt(punchdate.substring(8));
            int year = Integer.parseInt(punchdate.substring(0, 4));

            GregorianCalendar gc = new GregorianCalendar();

            gc.set(Calendar.DAY_OF_MONTH, day);
            gc.set(Calendar.YEAR, year);
            gc.set(Calendar.MONTH, month - 1);
            gc.set(Calendar.HOUR_OF_DAY, 0);
            gc.set(Calendar.MINUTE, 0);
            gc.set(Calendar.SECOND, 0);
            
            Badge b = db.getBadge(badgeid);
            Shift s = db.getShift(b);
            
            p.setOriginaltimestamp(gc.getTimeInMillis());
            Badge badge = db.getBadge(badgeid);
            p.setBadge(badge);
            
            ArrayList<Punch> punchList = db.getDailyPunchList(b, gc.getTimeInMillis());
            
            for (Punch punch : punchList) {
                punch.adjust(s);
            }
            
            String json = TASLogic.getPunchListPlusTotalsAsJSON(punchList, s);

            response.setContentType("text/html;charset=UTF-8");

            try (PrintWriter out = response.getWriter()) {

                out.println(json);

            }
            
        }
        
        catch (Exception e) {
            System.err.println(e.toString());
        }
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "PunchLookup Servlet";
    }

}
