package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Avalia extends HttpServlet
{

    private String user_bd;
    private String pw_bd;

    @Override
    public void init() 
    {
        user_bd = getServletContext().getInitParameter("user_bd");
        pw_bd = getServletContext().getInitParameter("pw_bd");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {

        // Pega a sessao
        HttpSession session = request.getSession(true);
        String code = request.getParameter("code");
        if (code != null && code.equals("sair")) {
            session.setAttribute("logado", false);
        }
        boolean logado = false;
        Object tmp = session.getAttribute("logado");
        if (tmp != null && (boolean) tmp) {
            logado = true;
        }

        String nome = request.getParameter("nomeHTML");
        String senha = request.getParameter("senhaHTML");

        if (!logado) {
            if (nome != null) {
                // quer fazer login
                if (validaLogin(nome, senha)) {
                    session.setAttribute("logado", true);
                    logado = true;
                } else {
                    session.setAttribute("msg", "Login inválido!");
                }
            } else {
                session.setAttribute("msg", "Sessão expirou!!");
                // expirou a sessao
            }
        }
        if (logado) {
            response.sendRedirect("menu.html");
        } else {
            response.sendRedirect("index.html");
        }
    }

    public boolean validaLogin(String nomeRecebido, String senhaRecebida) throws ServletException 
    {
        // Pega senha do banco de dados;
        boolean result = false;
        String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";
        String DB_URL = "jdbc:derby://localhost:1527/lpsw";
        //  Database credentials
        Connection conn = null;
        Statement stmt = null;
        String resp = "EXECUTOU";
        // Set response content type
        try {   
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);
            // conecta o banco com ususario e senha
            conn = DriverManager.getConnection(DB_URL, "caio", "123");
            // Execute SQL query
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT nome, senha FROM USUARIO where upper(nome) = '"
                    + nomeRecebido.toUpperCase() + "' and senha='" + senhaRecebida + "'";
            ResultSet rs = stmt.executeQuery(sql);
            // Extract data from result set
            if (rs.next()) {
                result = true;
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            //Handle errors for JDBC
            //throw new ServletException(e);
            resp = e.getMessage();
            throw new ServletException(e);
        } catch (Exception e) {
            //Handle errors for Class.forName
            //throw new ServletException(e);
            resp = e.getMessage();
            throw new ServletException(e);
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                throw new ServletException(e);
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new ServletException(e);
            }//end finally try
        } //end try    
        return result;
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
            throws ServletException, IOException {
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
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}