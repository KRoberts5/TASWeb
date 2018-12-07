package edu.jsu.mcis.tas.servlets;

import edu.jsu.mcis.tas.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import org.json.simple.JSONValue;

public class PunchEntry extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        try {
            
            TASDatabase db = new TASDatabase();
            Punch p = new Punch();
            HashMap<String, String> json = new HashMap<>();
            
            String badgeid = request.getParameter("badgeid").toUpperCase().trim();
            String punchdate = request.getParameter("punchdate").trim();
            String punchtime = request.getParameter("punchtime").trim();
            int punchtype = Integer.parseInt(request.getParameter("punchtype").trim());
            int terminalid = Integer.parseInt(request.getParameter("terminalid").trim());
            
            int month = Integer.parseInt(punchdate.substring(5, 7));
            int day = Integer.parseInt(punchdate.substring(8));
            int year = Integer.parseInt(punchdate.substring(0, 4));
            
            int hour = Integer.parseInt(punchtime.substring(0, 2));
            int minute = Integer.parseInt(punchtime.substring(3));

            GregorianCalendar gc = new GregorianCalendar();

            gc.set(Calendar.DAY_OF_MONTH, day);
            gc.set(Calendar.YEAR, year);
            gc.set(Calendar.MONTH, month - 1);
            gc.set(Calendar.HOUR_OF_DAY, hour);
            gc.set(Calendar.MINUTE, minute);
            gc.set(Calendar.SECOND, 0);
            
            p.setOriginaltimestamp(gc.getTimeInMillis());
            p.setAdjustedtimestamp(gc.getTimeInMillis());
            
            Badge badge = db.getBadge(badgeid);
            p.setBadge(badge);
            p.setPunchtypeid(punchtype);
            p.setTerminalid(terminalid);
            p.setEventdata("");
            
            int id = db.insertPunch(p);

            json.put("id", String.valueOf(id));
            
            System.err.println("Punch Added!");
            
            response.setContentType("text/html;charset=UTF-8");
            
            try (PrintWriter out = response.getWriter()) {

                out.println(JSONValue.toJSONString(json));

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
        return "PunchEntry Servlet";
    }

}
