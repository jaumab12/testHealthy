/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.Pizza;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Lukas
 */
@WebServlet(name = "PizzaServlet", urlPatterns =
{
    "/PizzaServlet"
})
public class PizzaServlet extends HttpServlet
{

    List<Pizza> list = new LinkedList<>();

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        readData();
    }

    private void readData()
    {
        String path = "/res" + File.separator + "pizzen.csv";
        //String path = getServletContext().getRealPath("/res" + File.separator + "pizzen.csv");
        ServletContext context = getServletContext();
        InputStream stream = context.getResourceAsStream(path);

        try
        {
            //FileReader fr = new FileReader(path);
            InputStreamReader isr = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(isr);
            String zeile = "";
            list.clear();
            while ((zeile = br.readLine()) != null)
            {
                String[] items = zeile.split(";");

                Pizza pizza = new Pizza(items[1], Double.parseDouble(items[2]), items[3]);
                list.add(pizza);
            }
            br.close();
            this.getServletContext().setAttribute("PizzaList", list);
        }
        catch (IOException ex)
        {
            System.out.println("Exception beim Einlesen einer Datei: " + ex);
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        Cookie[] arr = request.getCookies();
        Cookie langCookie = null;
        boolean cookieFound = false;
        if (arr != null)
        {
            for (Cookie cookie : arr)
            {
                if (cookie.getName().equals("language"))
                {
                    cookieFound = true;
                    langCookie = cookie;
                }
            }
        }
        if (!cookieFound)
        {
            langCookie = new Cookie("language", "de");
            response.addCookie(langCookie);
        }
        String lang = langCookie.getValue();

        String chosenLang = request.getParameter("language");
        if (chosenLang != null && !chosenLang.equals("") && !chosenLang.equals(lang))
        {
            langCookie.setValue(chosenLang);
            response.addCookie(langCookie);
            RequestDispatcher rq = request.getRequestDispatcher("PizzaServlet");
            rq.forward(request, response);
            return;
        }

        try (PrintWriter out = response.getWriter())
        {
            RequestDispatcher rq = request.getRequestDispatcher("html/angebot_" + lang + ".html");
            rq.include(request, response);
            for (int i = 0; i < list.size(); i++)
            {
                Pizza pizza = list.get(i);
                out.println("<tr" + ((i % 2 == 1) ? " class='odd'" : "") + ">");
                out.println("<td style='width:15%'><img src='img/" + pizza.getImageString() + "' /></td>");
                out.println("<td style='width:50%'>" + pizza.getName() + "</td>");
                out.println("<td style='width:15%'>" + String.format("%.2f €", pizza.getPrice()) + "</td>");

                out.println("<td><select style='width:35%' name='pizzaAmount" + i + "'>");
                for (int j = 0; j <= 10; j++)
                {
                    out.println("<option value='" + j + "'>" + j + "</option>");
                }
                out.println("</select></td>");

                out.println("</tr>");
            }
            out.println("</table>");
            String errorMsg = (String) request.getAttribute("errorMsg");
            if (errorMsg != null && !errorMsg.isEmpty())
            {
                out.println("<br /><div class='errormsg'>" + errorMsg + "</div><br />");
            }
            rq = request.getRequestDispatcher("html/angebot2_" + lang + ".html");
            rq.include(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>

}
