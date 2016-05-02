/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.Pizza;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Lukas
 */
@WebServlet(name = "BestellungsServlet", urlPatterns =
{
    "/BestellungsServlet"
})
public class BestellungsServlet extends HttpServlet
{

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

        HttpSession session = request.getSession();

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
        System.out.println(lang);
        String adresse = request.getParameter("lieferadresse");

        List<Pizza> list = (LinkedList<Pizza>) this.getServletContext().getAttribute("PizzaList");
        if (list == null)
        {
            RequestDispatcher rq = request.getRequestDispatcher("PizzaServlet");
            rq.forward(request, response);
            return;
        }
        HashMap<Integer, Integer> amounts = new HashMap(list.size());
        boolean amountsAbove1 = false;
        if (request.getParameter("languageSubmit") != null 
                && request.getParameter("languageSubmit").equals("true") 
                && request.getAttribute("languageChanged") != null 
                && request.getAttribute("languageChanged").equals(Boolean.TRUE))
        {
            adresse = (String) session.getAttribute("adresse");
            amounts = (HashMap<Integer, Integer>) session.getAttribute("amounts");
            amountsAbove1 = false;
            for (int i = 0; i < amounts.size(); i++)
            {
                int amount = amounts.get(i);
                if (amount > 0)
                {
                    amountsAbove1 = true;
                }
            }
            request.setAttribute("languageChanged", Boolean.FALSE);
        }
        else
        {
            for (int i = 0; i < list.size(); i++)
            {
                int amount = 0;
                try
                {
                    amount = Integer.parseInt(request.getParameter("pizzaAmount" + i));
                }
                catch (Exception ex)
                {
                    amount = 0;
                }
                if (amount > 0)
                {
                    amountsAbove1 = true;
                }
                amounts.put(i, amount);
            }
        }

        String chosenLang = request.getParameter("language");
        if ((request.getParameter("languageSubmit") != null 
                && request.getParameter("languageSubmit").equals("true") 
                && chosenLang != null 
                && !chosenLang.equals("") )
                ^ (request.getAttribute("languageChanged") != null 
                && !request.getAttribute("languageChanged").equals(Boolean.TRUE)))
        {
            langCookie.setValue(chosenLang);
            response.addCookie(langCookie);
            request.setAttribute("languageChanged", Boolean.TRUE);
            RequestDispatcher rq = request.getRequestDispatcher("BestellungsServlet");
            rq.forward(request, response);
            return;
        }
        else 
        {
            session.setAttribute("adresse", adresse);
            session.setAttribute("amounts", amounts);
        }

        if (!amountsAbove1)
        {
            request.setAttribute("errorMsg", ((lang.equals("en") 
                    ? "No pizza was selected" 
                    : "Es wurde keine einzige Pizza ausgewählt.")));
            RequestDispatcher rq = request.getRequestDispatcher("PizzaServlet");
            rq.forward(request, response);
            return;
        }

        try (PrintWriter out = response.getWriter())
        {

            if (adresse == null || adresse.isEmpty())
            {
                request.setAttribute("errorMsg", ((lang.equals("en") 
                        ? "No delivery address was entered" 
                        : "Es wurde keine gültige Lieferadresse angegeben.")));
                RequestDispatcher rq = request.getRequestDispatcher("PizzaServlet");
                rq.forward(request, response);
                return;
            }

            RequestDispatcher rq = request.getRequestDispatcher("html/bestellung_" + lang + ".html");
            rq.include(request, response);

            double sumAll = 0;
            for (int i = 0; i < list.size(); i++)
            {
                int amount = amounts.get(i);
                if (amount > 0)
                {
                    Pizza pizza = list.get(i);
                    out.println("<tr>");
                    out.println("<td style='width:50%'>" + pizza.getName() + "</td>");
                    out.println("<td style='width:15%'>" + String.format("%.2f €", pizza.getPrice()) + "</td>");
                    out.println("<td style='width:15%'>" + amount + "</td>");

                    double sumThisPizza = pizza.getPrice() * amount;
                    out.println("<td style='width:15%'>" + String.format("%.2f €", sumThisPizza) + "</td>");
                    sumAll += sumThisPizza;

                    out.println("</tr>");
                }
            }
            out.println("<tr class='sum'>");
            out.println("<td colspan='3' class='sum'><b>" + (lang.equals("en") ? "Sum:" : "Summe") + "</b></td>");
            out.println("<td style='border-top: 2px solid white'>" + String.format("%.2f €", sumAll) + "</td>");
            out.println("</tr>");
            out.println("<tr><td>" + (lang.equals("en") ? "Delivery Address" : "Lieferadresse") + ": " + adresse + "</td></tr>");

            rq = request.getRequestDispatcher("html/bestellung2_" + lang + ".html");
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
